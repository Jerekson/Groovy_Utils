/*
* Authors: Koceila HADDOUCHE & Dimitri TORTERAT
* Date: 21/02/2020
*/

// give me the right for a specific user
whichRights = { priv ->
    priv -= 14 // First least-significant bits aren’t used
    
    def allRights = [
        1:      'Log-in enabled',
//      2:      '[reserved]',
//      4:      '[reserved]',
//      8:      '[reserved]',
        16:     'User administration rights',
        32:     'Can create/modify users',
        64:     'Can create/modify groups',
        256:    'System administration rights',
        2048:   'Public Access enabled',

        128:    '[unknown permission (128)]',
        512:    '[unknown permission (512)]',
        1024:   '[unknown permission (1024)]',
        4096:   '[unknown permission (4096)]',
    ]
    def myRights = []

    allRights.each { permNum, permLabel ->
        if (priv & permNum) {
            // Current bit of permNum matches one in allRights
            myRights << permLabel
        }
    }
    return myRights
}

userRequest = """
SELECT
	k.ID,
	k.Name AS Username,
	k.FirstName,
	k.LastName,
	k.UserPrivileges as Privileges
FROM KUAF k
WHERE
	k.Type = 0 
    AND k.ID != 1000
	AND k.Deleted = 0;
"""

out << '''\
<style>
#ose-perm-list dt {
  font-weight: bold;
  cursor: help;
}
#ose-perm-list dt:not(:first-child) {
  margin-top: 1em
}
</style>'''

out << '<p>All rights</p>'
out << '<ul>'
whichRights(0b1111_1111_1111_1000)?.each {
    out << "<li>${it}</li>"
}
out << '</ul>'

out << '<dl id="ose-perm-list">'
sql.runSQL(userRequest)?.rows?.each { row ->
    def userDetails = "${row?.FirstName} ${row?.LastName} (#${row?.ID})"
    out << """<dt title="${userDetails}">${row?.FirstName}</dt>"""
    
    whichRights(row?.Privileges)?.each {
        out << "<dd>${it}</dd>"
    }
}
out << '</dl>'
