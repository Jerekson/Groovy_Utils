def csvTemplate = asCSNode(nickname:"SGDBF_ADMINISTRATION_USERS_TO_ADD_CSV").content.content
def csvContent = csvTemplate.getText('utf-8')

csvContent.readLines().each{
    out << it
    out << "<br>"
}