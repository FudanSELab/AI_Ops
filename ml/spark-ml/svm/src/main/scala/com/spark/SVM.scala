package com.spark

import org.apache.spark.ml.classification.{LinearSVC, OneVsRest, OneVsRestModel}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder, TrainValidationSplit}
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.functions.rand
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.util.Random

object SVM extends App {

//    val master = "yarn"
//    val filePath = "hdfs://10.141.211.173:8020/user/admin/mock.csv"
    val master = "local"
    val filePath = "final_after_dimensionality_reduction.csv"
    val appName = "Spark SVM"

    println("[Run]SVM Main")

    val spark = SparkSession
      .builder()
      .appName(appName)
      .master(master)
      .getOrCreate()

    val dataDF = spark.read.format("csv")
      .option("sep", ",")
      .option("inferSchema", "true")
      .option("header", "true")
      .load(filePath)

    val columns_list = dataDF.columns
    val feature_list = columns_list.slice(1, columns_list.length-1)

    val assembler = new VectorAssembler()
      .setInputCols(feature_list)
      .setOutputCol("features")
    val vecDF: DataFrame = assembler.transform(dataDF)
    vecDF.orderBy(rand())

    vecDF.show(5)

    val featureAndLabel: DataFrame = vecDF.select("features", "y")
    featureAndLabel.show(5)

    val Array(trainingData, testData) = featureAndLabel.randomSplit(Array(0.8, 0.2))

    val lsvc = new LinearSVC()
      .setMaxIter(10)
      .setRegParam(0.1)
      .setLabelCol("y")
      .setFeaturesCol("features")

    // Instantiate the One Vs Rest Classifier.
    val ovr = new OneVsRest()
      .setClassifier(lsvc)
      .setLabelCol("y")
      .setFeaturesCol("features")
      .setPredictionCol("prediction")

    val pipeline = new Pipeline().setStages(Array(ovr))
    val pipelineModel = pipeline.fit(trainingData)

    val predictions = pipelineModel.transform(testData)
    predictions.show(5)

    // obtain evaluator.
    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("y")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")

    // compute the classification error on test data.
    val accuracy = evaluator.evaluate(predictions)
    println(s"Test Error = ${1 - accuracy}")

    pipelineModel.write.overwrite().save("model/svm/pipeline_model")
    val samePipelineModel = PipelineModel.load("model/svm/pipeline_model")

    // We use a ParamGridBuilder to construct a grid of parameters to search over.
    // TrainValidationSplit will try all combinations of values and determine best model using
    // the evaluator.
    val paramGrid = new ParamGridBuilder()
      .addGrid(lsvc.regParam, Seq(0.1, 0.2))
      .addGrid(lsvc.maxIter, Seq(10, 20))
      .build()
    // In this case the estimator is simply the linear regression.
    // A TrainValidationSplit requires an Estimator, a set of Estimator ParamMaps, and an Evaluator.
    val multiclassEval = new MulticlassClassificationEvaluator()
      .setLabelCol("y")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val trainValidationSplit = new CrossValidator()
      .setSeed(Random.nextLong())
      .setEstimator(pipeline)
      .setEvaluator(multiclassEval)
      .setEstimatorParamMaps(paramGrid)
      .setNumFolds(5)// 80% of the data will be used for training and the remaining 20% for validation.

    // Run train validation split, and choose the best set of parameters.
    val validatorModel = trainValidationSplit.fit(trainingData)

    val bestPipelineModel = validatorModel.bestModel.asInstanceOf[PipelineModel]
    val bestSVM = bestPipelineModel.stages(0).asInstanceOf[OneVsRestModel]

    val paramsAndMetrics = validatorModel.avgMetrics.zip(validatorModel.getEstimatorParamMaps).sortBy(-_._1)

    paramsAndMetrics.foreach{ case (metric, params) =>
      println(metric)
      println(params)
      println()
    }

    // Make predictions on test data. model is the model with combination of parameters
    // that performed best.
    bestPipelineModel.transform(testData)
      .select("features", "y", "prediction")
      .show(400)
}
