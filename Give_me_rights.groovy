
/* // don't understand this code for now
def parms=args[0]

def devGroup = users.getGroupByName("AEROW_DEVELOPERS")

if (parms.user && users.getUserByLoginName(parms.user)) {
    users.addMemberToGroup(devGroup, users.getUserByLoginName(parms.user))
}
*/
// get the node id of the above code : here -> 108155

runCS("108155", ["user":users.CURRENTUSER().name])

out << "Job done <br/><a href='${params.nexturl}'>Continue</a>"