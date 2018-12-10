package com.spark

import org.apache.spark.ml.classification.{DecisionTreeClassificationModel, DecisionTreeClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder, TrainValidationSplit}
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.rand
import scala.util.Random


object DT extends App {

//  val master = "yarn"
//  val filePath = "hdfs://10.141.211.173:8020/user/admin/mock.csv"
  val master = "local"
  val filePath = "final_after_dimensionality_reduction.csv"
  val appName = "Spark Decision Tree"

  println("[Run]Decision Main")

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
  vecDF.orderBy(rand())

  vecDF.show(5)

  val featureAndLabel: DataFrame = vecDF.select("features", "y")
  featureAndLabel.show(5)

  val Array(trainingData, testData) = featureAndLabel.randomSplit(Array(0.8, 0.2))

  val dt = new DecisionTreeClassifier()
    .setSeed(Random.nextLong())
    .setLabelCol("y")
    .setFeaturesCol("features")
    .setPredictionCol("prediction")

  val pipeline = new Pipeline().setStages(Array(dt))
  val pipelineModel = pipeline.fit(trainingData)

  val predictions = pipelineModel.transform(testData)
  predictions.show(5)

  val evaluator = new MulticlassClassificationEvaluator()
    .setLabelCol("y")
    .setPredictionCol("prediction")
    .setMetricName("accuracy")

  val accuracy = evaluator.evaluate(predictions)
  println("Test Error = " + (1.0 - accuracy))

  val treeModel = pipelineModel.stages(0).asInstanceOf[DecisionTreeClassificationModel]
  println("Model:\n" + treeModel.toDebugString)

  pipelineModel.write.overwrite().save("model/dt/pipeline_model")
  val samePipelineModel = PipelineModel.load("model/dt/pipeline_model")

  // We use a ParamGridBuilder to construct a grid of parameters to search over.
  // TrainValidationSplit will try all combinations of values and determine best model using
  // the evaluator.
  val paramGrid = new ParamGridBuilder()
    //.addGrid(dt.impurity, Seq("gini", "entropy"))
    .addGrid(dt.maxDepth, Seq(5, 10, 20, 30))
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
  val bestTreeModel = bestPipelineModel.stages(0).asInstanceOf[DecisionTreeClassificationModel]

  val paramsAndMetrics = validatorModel.avgMetrics.zip(validatorModel.getEstimatorParamMaps).sortBy(-_._1)

  paramsAndMetrics.foreach{ case (metric, params) =>
    println(metric)
    println(params)
    println()
  }

  // Make predictions on test data. model is the model with combination of parameters
  // that performed best.
  bestPipelineModel.transform(testData)
//    .select("features", "y1", "prediction")
    .show()
}


