# [Discord]: Create Role command
## What does it do?
This command creates a new role, to create give players, who should access the channel, to send / recieve minecraft messages to/from. **You still need to apply this role to the channel individually!!!**

---

## How to use it?
Command: `prefix`+`createRole` (not case sensitive) <br>
Arguments:
- the name of the role to create **(in quotes, if you use spaces in your name!)**

---

## Which permissions are required?
### For the user
See configuration: <br>
permission.discordServerAdmin.allowedUserIDs <br> <br>
The users [ID](./../get-channel-id#user-id) must be present in this configuratin list entry. Or use `config add permission.discordServerAdmin.allowedUserIDs THE_ID` to add the users ID to the trusted IDs.

### For the bot
The bot must have the permission to create and manage roles. For the best, the bot would have Administrator priviledges.

---

## Example
Example: `.createRole "Minecraft SMP"` <br> <br>
Send the command to a channel, the bot can read and write. <br>
![Send this message](./../assets/commands/createRole/1.png) <br>
You'll get this reply and a new role was creatd. <br>
![This is the reply in the same channel](./../assets/createRole/setup/2.png) <br>
In the newly created channel, the bot will have sent this message: <br>
![Message in the new Channel](./../assets/commands/createRole/3.png) <br>

---

## On wrong usage
![Reply on a wrong usage](./../assets/commands/createRole/help.png)
