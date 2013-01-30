NuVote
------

NuVote is an easy way to hold polls and votes in Minecraft, it is an easily configurable bukkit plugin.

To set up, simply change a few values in the plugins config and you are ready to go.

#### An example config ####

    questions:
      list:
      - This is an item!
      - Item 2
      - Item 3
      - This is a variable length list of options!
      boradcast: Vote now, use the /vote <id> command!
    vote:
      c45y: '1'
      user1: '1'
      user2: '2'

#### Explanation ####

The questions header can be ignored, it is the sub values `list` and `broadcast` that are important.

**questions.list** is where you can set your voteing options, such as next map type, build content winner, *etc*

**questions.broadcast** is a broadcasted message on a rotating timer, use this to inform users what they are voting on as well as how to vote

**vote** is where all the voting data will be stored, beside each username ( *vote.c45y* ) there is a number, this number is their choice from the list of votes provided under the *questions.list* config item