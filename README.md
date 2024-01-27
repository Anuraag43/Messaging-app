# Messaging-app 

### We have a Sender which mimics the Rabbit Producer. Currently it sends a message every three seconds. We have an "ActiveQueue" in the app which acts as a producer.

### In the working hour period, messages from producer are consumed instantly and processed further (in our demo it prints an acknowledgement to console). 

### In in off hour period, we have an exchange which takes in messages from producer and waits a given amount of time before sending them to consumer. We have an OffHourQueue which depicts this.

Link to a screen recording of demo:

https://docs.google.com/presentation/d/16WOuFN08XNGqj5wqpe0iOyGpnyY6Eom-/edit?usp=drive_link&ouid=105135576376865388070&rtpof=true&sd=true
