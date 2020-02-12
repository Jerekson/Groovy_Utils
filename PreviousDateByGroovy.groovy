import java.time.*
import java.time.format.DateTimeFormatter

Date unedate = new Date().clearTime()

out << "date d ajd -> ${unedate}<br>"

pv = unedate.previous()

out << "hier ? -> ${pv.format('MM/dd/yyyy')}"