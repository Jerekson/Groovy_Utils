SELECT
	k.ID,
	k.Name AS Username,
	k.FirstName,
	k.LastName,
	k.UserPrivileges as Privileges
FROM KUAF k
WHERE
	k.Type = 0 
	AND k.Deleted = 0
	AND k.UserPrivileges & 256 > 0;