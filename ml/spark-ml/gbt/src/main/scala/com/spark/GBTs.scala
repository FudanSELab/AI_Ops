package com.spark

import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.{GBTClassifier, OneVsRest, OneVsRestModel}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder, TrainValidationSplit}
import org.apache.spark.sql.functions.rand
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.util.Random


object GBTs extends App {

  //  val master = "yarn"
  //  val filePath = "hdfs://10.141.211.173:8020/user/admin/mock.csv"
  val master = "local"
  val filePath = "final_after_dimensionality_reduction.csv"
  val appName = "Spark Gradient-boosted tree classifier"

  println("[Run]GBTs Main")

  val spark = SparkSession
    .builder()
    .appName(appName)
    .master(master)
    .getOrCreate()

  val dataDF = spark.read
    .format("csv")
    .option("sep", ",")
    .option("inferSchema", "true")
    .option("header", "true")
    .load(filePath)


  val all_columns_list = dataDF.columns
  val only_feature_list = all_columns_list.slice(1, all_columns_list.length-1)

  val assembler = new VectorAssembler()
    .setInputCols(only_feature_list)  
    .setOutputCol("features")
  val vecDF: DataFrame = assembler.transform(dataDF)

  val featureAndLabel: DataFrame = vecDF.select("features", "y")
  vecDF.orderBy(rand())


  val Array(trainingData, testData) = featureAndLabel.randomSplit(Array(0.8, 0.2))

  val gbt = new GBTClassifier()
    .setLabelCol("y")
    .setFeaturesCol("features")
    .setMaxIter(3)
    .setFeatureSubsetStrategy("auto")
    .setPredictionCol("prediction")

  // Instantiate the One Vs Rest Classifier.
  val ovr = new OneVsRest()
    .setClassifier(gbt)
    .setLabelCol("y")
    .setFeaturesCol("features")
    .setPredictionCol("prediction")

  val pipeline = new Pipeline().setStages(Array(ovr))
  val pipelineModel = pipeline.fit(trainingData)

  val predictions = pipelineModel.transform(testData)
  predictions.show(5)

  val evaluator = new MulticlassClassificationEvaluator()
    .setLabelCol("y")
    .setPredictionCol("prediction")
    .setMetricName("accuracy")

  val accuracy = evaluator.evaluate(predictions)
  println("Test Error = " + (1.0 - accuracy))


  pipelineModel.write.overwrite().save("model/gbts/pipeline_model")
  val samePipelineModel = PipelineModel.load("model/gbts/pipeline_model")

  // We use a ParamGridBuilder to construct a grid of parameters to search over.
  // TrainValidationSplit will try all combinations of values and determine best model using
  // the evaluator.
  val paramGrid = new ParamGridBuilder()
    //.addGrid(gbt.impurity, Seq("gini", "entropy"))
    .addGrid(gbt.maxDepth, Seq(5, 10, 20, 30))
    .addGrid(gbt.maxIter, Seq(5, 20))
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
    .setNumFolds(10)// 80% of the data will be used for training and the remaining 20% for validation.

  // Run train validation split, and choose the best set of parameters.
  val validatorModel = trainValidationSplit.fit(trainingData)

  val bestPipelineModel = validatorModel.bestModel.asInstanceOf[PipelineModel]
  val bestOVRModel = bestPipelineModel.stages(0).asInstanceOf[OneVsRestModel]

  val paramsAndMetrics = validatorModel.avgMetrics.zip(validatorModel.getEstimatorParamMaps).sortBy(-_._1)

  paramsAndMetrics.foreach{ case (metric, params) =>
    println(metric)
    println(params)
    println()
  }

  // Make predictions on test data. model is the model with combination of parameters
  // that performed best.
  bestPipelineModel.transform(testData)
    .show(400)
}
