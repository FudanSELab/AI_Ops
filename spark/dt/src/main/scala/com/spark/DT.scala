package com.spark

import org.apache.spark.ml.classification.{DecisionTreeClassificationModel, DecisionTreeClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.tuning.{ParamGridBuilder, TrainValidationSplit}
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.{DataFrame, SparkSession}
import scala.util.Random


object DT extends App {
  println("Hello, World!")
  //执行程序之前首先进行以下设定
  val master = "yarn"
  val appName = "Spark SQL basic example"
  val filePath = "hdfs://10.141.211.173:8020/user/admin/mock.csv"

  println("[Run]Decision Main")

  // 设置Spark运行时的参数
  val spark = SparkSession
    .builder()
    .appName(appName)
    .master(master)
    .getOrCreate()

  // 读取文件，注意设置格式和文件路径
  val dataDF = spark.read.format("csv")
    .option("sep", ",")
    .option("inferSchema", "true")
    .option("header", "true")
    .load(filePath)

  // 文件表格中的列，和我们需要的属性列（丢弃第一列序号列和最后一列label列）
  val columns_list = dataDF.columns
  val feature_list = columns_list.slice(1, columns_list.length-1)

  // 把每一行属性的值拿出来组装成向量，放进"features"列中
  val assembler = new VectorAssembler()
    .setInputCols(feature_list)
    .setOutputCol("features")
  val vecDF: DataFrame = assembler.transform(dataDF)
  vecDF.show(5)

  // 把features列和label列拿出来，准备后续训练数据（这里的label列是y1）
  val featureAndLabel: DataFrame = vecDF.select("features", "y1")
  featureAndLabel.show(5)

  // 分割训练集和测试集合
  val Array(trainingData, testData) = featureAndLabel.randomSplit(Array(0.8, 0.2))

  // 建立决策树模型，设置其参数
  val dt = new DecisionTreeClassifier()
    .setSeed(Random.nextLong())
    .setLabelCol("y1")             //在这里设置label列
    .setFeaturesCol("features")    //在这里设置feature列
    .setPredictionCol("prediction")

  // 组装Pipeline并进行模型训练，注意这个模型是是Pipeline整个流水线的模型
  val pipeline = new Pipeline().setStages(Array(dt))
  val pipelineModel = pipeline.fit(trainingData)

  // 用模型来计算测试数据
  val predictions = pipelineModel.transform(testData)
  predictions.show(5)

  // 计算准确度信息等
  val evaluator = new MulticlassClassificationEvaluator()
    .setLabelCol("y1")
    .setPredictionCol("prediction")
    .setMetricName("accuracy")

  val accuracy = evaluator.evaluate(predictions)
  println("Test Error = " + (1.0 - accuracy))

  // 把整个Pipeline模型的第一部分拿出来（pipeline根据之前的设定第一个为决策树模型），输出决策树判别细节
  val treeModel = pipelineModel.stages(0).asInstanceOf[DecisionTreeClassificationModel]
  println("Learned classification tree model:\n" + treeModel.toDebugString)

  //模型的读取与存储
  pipelineModel.save("model/dt/pipeline_model")
  val samePipelineModel = PipelineModel.load("model/dt/pipeline_model")

  // 寻找最佳超参数：一下部分用于寻找最优超参数
  // We use a ParamGridBuilder to construct a grid of parameters to search over.
  // TrainValidationSplit will try all combinations of values and determine best model using
  // the evaluator.
  val paramGrid = new ParamGridBuilder()
    .addGrid(dt.impurity, Seq("gini", "entropy"))        //不纯度函数
    .addGrid(dt.maxDepth, Seq(5, 20))      //树的最大深度尝试
    .addGrid(dt.maxBins, Seq(40, 200))      //
    .addGrid(dt.minInfoGain, Seq(0.0, 0.05)) //
    .addGrid(dt.maxDepth, Seq(5, 20))      //树的最大深度尝试
    .addGrid(dt.minInstancesPerNode, Seq(10, 30))  //每个节点的最少元素数量
    .build()                                             //还可以有很多的超参数
  // In this case the estimator is simply the linear regression.
  // A TrainValidationSplit requires an Estimator, a set of Estimator ParamMaps, and an Evaluator.
  val multiclassEval = new MulticlassClassificationEvaluator()
    .setLabelCol("y1")
    .setPredictionCol("prediction")
    .setMetricName("accuracy")
  val trainValidationSplit = new TrainValidationSplit()
    .setSeed(Random.nextLong())
    .setEstimator(pipeline)
    .setEvaluator(multiclassEval)
    .setEstimatorParamMaps(paramGrid)
    .setTrainRatio(0.8)// 80% of the data will be used for training and the remaining 20% for validation.

  // Run train validation split, and choose the best set of parameters.
  val validatorModel = trainValidationSplit.fit(trainingData)

  // 从训练好的Validator中拿到最好的PipelineModel和DTModel
  val bestPipelineModel = validatorModel.bestModel.asInstanceOf[PipelineModel]
  val bestTreeModel = bestPipelineModel.stages(0).asInstanceOf[DecisionTreeClassificationModel]

  //输出Validor中各个模型的参数
  val paramsAndMetrics = validatorModel.validationMetrics
    .zip(validatorModel.getEstimatorParamMaps).sortBy(-_._1)
  paramsAndMetrics.foreach{ case (metric, params) =>
    println(metric)
    println(params)
    println()
  }

  // Make predictions on test data. model is the model with combination of parameters
  // that performed best.
  bestPipelineModel.transform(testData)
    .select("features", "y1", "prediction")
    .show()

}


