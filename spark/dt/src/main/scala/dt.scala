import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.{DataFrame,SparkSession}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.DecisionTreeClassificationModel
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator


object dt {

  def main(args: Array[String]): Unit = {

    println("Decision Main")

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

    val dt = new DecisionTreeClassifier()
      .setLabelCol("y1")
      .setFeaturesCol("features")
      .setImpurity("gini")
      .setMaxDepth(30)

    println("DecisionTreeClassifier parameters:\n" + dt.explainParams() + "\n")

    val pipeline = new Pipeline().setStages(Array(dt))

    val model = pipeline.fit(trainingData)

    val predictions = model.transform(testData)

    predictions.show(5)

    val evaluator = new MulticlassClassificationEvaluator().setLabelCol("y1").setPredictionCol("prediction")

    val accuracy = evaluator.evaluate(predictions)

    println("Test Error = " + (1.0 - accuracy))

    val treeModel = model.stages(0).asInstanceOf[DecisionTreeClassificationModel]

    println("Learned classification tree model:\n" + treeModel.toDebugString)



  }


}