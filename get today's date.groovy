import java.text.SimpleDateFormat

try{
    //Date 
    Date aujourdhui = new Date();
    formater = new SimpleDateFormat("'le' dd/MM/yyyy 'à' HH'h'mm");
    
    out << formater.format(aujourdhui)
    
}catch(e){
    out << e
}