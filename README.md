# RSSFeedDownloaderApp
 
In this app, the user has the ability to select an option from free apps/ paid apps/ songs / movies and according to the option selected
the relevent RSS Feed data is downloaded in the form of XML by setting up a HTTP Url connedtion.
Then this XML is parsed using the XML Pull Parser and converted to a list of objects with the help of Async Task.
Then this list of objects is shown to user in user-friendly manner using the custom adapter and a resusable ListView.
