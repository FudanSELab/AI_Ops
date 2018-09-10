import tornado.ioloop
import tornado.web
import pre_train_y
import predict_y
import verify_y
import re_train_y


class PreTrainHandler(tornado.web.RequestHandler):
    def get(self):
        print("[Y Server] GET in PreTrainHandler")
        pre_train_y.pre_train()


class PredictHandler(tornado.web.RequestHandler):
    def get(self):
        print("[Y Server] GET in PredictHandler")
        predict_feature = ['212212', '1', '2', "3", "4", "5", "6", "7"]
        predict_y.predict(predict_feature)


class VerifyHandler(tornado.web.RequestHandler):
    def get(self):
        print("[Y Server] GET in VerifyHandler")
        verify_feature = ['212212', '1', '2', "3", "4", "5", "6", "7"]
        verify_y.verify(verify_feature)


class ReTrainHandler(tornado.web.RequestHandler):
    def get(self):
        print("[Y Server] GET in ReTrainHandler")
        re_train_feature = ['212212', '1', '2', "3", "4", "5", "6", "7"]
        re_train_y.re_train(re_train_feature)


class DescriptionHandler(tornado.web.RequestHandler):
    def get(self):
        self.write("Server for train & predict Y. \n"
                   "Listening on Port 16101")


def make_app():
    return tornado.web.Application([
        (r"/pre_train", PreTrainHandler),
        (r"/predict", PredictHandler),
        (r"/verify", VerifyHandler),
        (r"/re_train", ReTrainHandler),
        (r"/", DescriptionHandler),

    ])


if __name__ == "__main__":
    app = make_app()
    app.listen(21001)
    tornado.ioloop.IOLoop.current().start()
