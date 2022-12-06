def collection1 = ["test", "a"]
def collection2 = ["test", "b"]
def commons = collection1.intersect(collection2)
def difference = collection1.plus(collection2)
difference.removeAll(commons)
out << difference
