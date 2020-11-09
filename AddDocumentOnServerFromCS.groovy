// ajout de document du CS dans un serveur (avec ou sans droits)
def ids = [node1, node2]
ids.each{
    def docs = docman.getNode(it).content.content
    File copy = new File("Chemin\\${"doc.getName()}")
    copy << doc.bytes
}