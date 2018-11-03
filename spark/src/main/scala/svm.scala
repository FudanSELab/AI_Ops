import org.apache.spark.ml.classification.{LinearSVC, OneVsRest}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.{DataFrame, SparkSession}

object svm {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("Spark SQL basic example")
      .master("local")
      .getOrCreate()

    val dataDF = spark.read.format("csv")
      .option("sep", ",")
      .option("inferSchema", "true")
      .option("header", "true")
      .load("mock.csv")

    val columns_list = dataDF.columns

    val feature_list_len = columns_list.length - 2

    val feature_list = new Array[String](feature_list_len)
    //丢弃序号列，丢弃最后面的label列
    for( i <- 0 until feature_list.length){
      feature_list(i) = columns_list(i+1)
      println(feature_list(i))
    }

    // 字段转换成特征向量
    val assembler = new VectorAssembler().setInputCols(feature_list).setOutputCol("features")
    val vecDF: DataFrame = assembler.transform(dataDF)
    val featureAndLabel: DataFrame = vecDF.select("features", "y1")
    featureAndLabel.show(5)

    val Array(trainingData, testData) = featureAndLabel.randomSplit(Array(0.8, 0.2))

    val lsvc = new LinearSVC()
      .setMaxIter(10)
      .setRegParam(0.1)
      .setLabelCol("y1")
      .setFeaturesCol("features")

//    // Fit the model
//    val lsvcModel = lsvc.fit(trainingData)

    // instantiate the One Vs Rest Classifier.
    val ovr = new OneVsRest()
      .setClassifier(lsvc)
      .setLabelCol("y1")
      .setFeaturesCol("features")

    // train the multiclass model.
    val ovrModel = ovr.fit(trainingData)

    // score the model on test data.
    val predictions = ovrModel.transform(testData)

    // obtain evaluator.
    val evaluator = new MulticlassClassificationEvaluator()
      .setMetricName("accuracy")
      .setLabelCol("y1")

    // compute the classification error on test data.
    val accuracy = evaluator.evaluate(predictions)
    println(s"Test Error = ${1 - accuracy}")




  }
}
