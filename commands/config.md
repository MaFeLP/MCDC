# [Minecraft]: Config command
## What does it do?

This command enables you to change values in this plugins configuration.

---

## How to use it?
In the console:
```
config <subcommand> <path> [<value>]
```

As a player:
```
/config <subcommand> <path> [<value>]
```

### Subcommands
<table>
    <tr>
        <th>Subcommand</th>
        <th>Description</th>
        <th>Requires Value <br> Option</th>
    </tr>
    <tr>
        <td>reload</td>
        <td>Loads and overrides the configuration stored in memory and replaces it with the configuration from the configuration file.</td>
        <td><code>true</code></td>
    </tr>
    <tr>
        <td>save</td>
        <td>Saves the current state of the configuration, which is currently loaded, to the file. This is also ebing executed on disable of this plugin.</td>
        <td><code>true</code></td></tr>
    <tr>
        <td>default</td>
        <td>Restores the configuration to its defaults.</td>
        <td><code>false</code><br>Also does not <br>require PATH value</td>
    </tr>
    <tr>
        <td>set</td>
        <td>Sets a PATH in the configuration to the VALUE</td>
        <td><code>true</code></td>
    </tr>
    <tr>
        <td>get</td>
        <td>Gets the value, specified in the PATH</td>
        <td><code>false</code></td>
    </tr>
    <tr>
        <td>add</td>
        <td>Adds a value to a list from a list of values, specifeid in PATH.</td>
        <td><code>true</code></td>
    </tr>
    <tr>
        <td>remove</td>
        <td>Removes a value from a list of values, specified in PATH.</td>
        <td><code>true</code></td>
    </tr>
</table>


---

## Which permissions are required?
If you execute this command in the console, you do not have to have any permissions, because the console can execute **any** MCDC commands.

### For the user
#### Using OP Level
The user needs to have an OP Level equal or higher than the level specified in the config file: `permission.configEdit.level` Get the value for this, using the command
```
config get permission.configEdit.level
```

in the console. <br><br>
To change the OP level, go to your servers main directory, open the `opsn.json` file and edit the value `"level": 4,`. Here replace the number 4 with the level you want to give the player, whose name is specified in the line above. If your user does not appear in this list, use the command `op USERNAME` in the console, to add the user to the list of OPs.

#### Using Wildcards
A users UUID must be specified in `permission.configEdit.allowedUserUUIDs` for the user to edit the configuration file, without having the required OP level. To add a user to this list, use
```
config add permission.configEdit.allowedUserUUIDs THE_UUID_OF_THE_PLAYER
```

You get the UUID, when a player joins the server in the servers console.
