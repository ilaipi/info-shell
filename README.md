### for a gather flow ,there are beans:

-  `shell`    do the cycle for all info
  
-  `resultHandler`    handle all fields of gathered
  
-  `listPage`    tell the info list and subpage-link to system
  
-  `global-field`    gather using the whole listpage html
  
-  `constant-field`    set some constant,treated as global-field
  
-  `list-field`    gather using the list tag html
  
-  `page`   map the sub page of list page,or deeper page
  
-  `locate`    before get the field value,you can locate a little html make get value easier
  
-  `field-value`    get the value from html,you can use plain text,regx and link...
  
-  `field-value-handler`    after get the value ,you can use this to do sth about the value,e.g. replace \s with ''
  
after you config all beans ok,you can set a task for the flow using the cron...

  The project use mybatis 3 to access the db,and all field gathered put in map while gather running, 
  
  and at last the map will be saved by `mybatis`.
=======
# info-shell
gather news from any website
