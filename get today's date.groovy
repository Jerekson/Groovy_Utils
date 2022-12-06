import java.text.SimpleDateFormat

try{
    //Date 
    Date today = new Date();
    formater = new SimpleDateFormat("'le' dd/MM/yyyy 'à' HH'h'mm");
    newDate = formater.format(today)
    out << newDate
    
}catch(e){
    out << e
}