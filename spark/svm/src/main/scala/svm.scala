import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.{LinearSVC, OneVsRest, OneVsRestModel}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.tuning.{ParamGridBuilder, TrainValidationSplit}
import org.apache.spark.sql.{DataFrame, SparkSession}
import scala.util.Random

object svm {

  def main(args: Array[String]): Unit = {

    //执行程序之前首先进行以下设定
    val master = "local"
    val appName = "Spark SQL basic example"
    val filePath = "mock.csv"

    println("[Run]SVM Main")

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

    val lsvc = new LinearSVC()
      .setMaxIter(10)
      .setRegParam(0.1)
      .setLabelCol("y1")
      .setFeaturesCol("features")

    // Instantiate the One Vs Rest Classifier.
    val ovr = new OneVsRest()
      .setClassifier(lsvc)
      .setLabelCol("y1")             //在这里设置label列
      .setFeaturesCol("features")    //在这里设置feature列
      .setPredictionCol("prediction")

    // 组装Pipeline并进行模型训练，注意这个模型是是Pipeline整个流水线的模型
    val pipeline = new Pipeline().setStages(Array(ovr))
    val pipelineModel = pipeline.fit(trainingData)

    // 用模型来计算测试数据
    val predictions = pipelineModel.transform(testData)
    predictions.show(5)

    // obtain evaluator.
    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("y1")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")

    // compute the classification error on test data.
    val accuracy = evaluator.evaluate(predictions)
    println(s"Test Error = ${1 - accuracy}")

    //模型的读取与存储
    pipelineModel.save("model/svm/pipeline_model")
    val samePipelineModel = PipelineModel.load("model/svm/pipeline_model")

    // 寻找最佳超参数：一下部分用于寻找最优超参数
    // We use a ParamGridBuilder to construct a grid of parameters to search over.
    // TrainValidationSplit will try all combinations of values and determine best model using
    // the evaluator.
    val paramGrid = new ParamGridBuilder()
      .addGrid(lsvc.regParam, Seq(0.1, 0.2))        //不纯度函数
      .addGrid(lsvc.maxIter, Seq(10, 20))
      .build()
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
    val bestSVM = bestPipelineModel.stages(0).asInstanceOf[OneVsRestModel]

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
}
