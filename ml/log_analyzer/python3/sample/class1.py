class Foo:

  def __init__(self, name, age):
    self.name = name
    self.age = age
  
  def detail(self):
    print(self.name)
    print(self.age)
  
obj1 = Foo('chengd', 18)
obj1.detail()  # Python默认会将obj1传给self参数，即：obj1.detail(obj1)，所以，此时方法内部的 self ＝ obj1，即：self.name 是 chengd ；self.age 是 18
  
obj2 = Foo('python', 99)
obj2.detail()  # Python默认会将obj2传给self参数，即：obj1.detail(obj2)，所以，此时方法内部的 self ＝ obj2，即：self.name 是 python ； self.age 是 99x



print(globals()["Foo"])('xxx', 0)












