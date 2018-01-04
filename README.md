# Wowza ReloadSchedule Module
The **ReloadSchedule** module for [Wowza Streaming Engine™ media server software](https://www.wowza.com/products/streaming-engine) adds custom HTTP Provider
to control (reload or unload) stream schedules created by the [StreamPublisher Module](https://www.wowza.com/docs/how-to-schedule-streaming-with-wowza-streaming-engine-streampublisher).

## Prerequisites
Wowza Streaming Engine 4.0.0 or later is recommended.

## Usage
Install compiled JAR file in the [install-dir]/lib folder and add a module to the [install-dir]/conf/VHost.xml file under HostPort 8086 section as follows:

```xml
<HTTPProvider>
    <BaseClass>com.serverlabs.wms.plugin.HTTPReloadSchedule</BaseClass>
    <RequestFilters>scheduleloader*</RequestFilters>
    <AuthenticationMethod>none</AuthenticationMethod>
</HTTPProvider>
```

Module configuration must be placed above any existing HTTP Providers with a wildcard in RequestFilter property (ex. <RequestFilters>*</RequestFilters>).
Since this wildcard acts as a catchall for any previously unmatched request filters, anything placed below it will not be executed.

The AuthenticationMethod property specifies the authentication method that's used to control access to the HTTP Provider.
Valid values are admin-digest admin-basic and none. The admin-digest authentication method uses digest authentication to control access to the HTTP Provider.
User names and passwords for admin-digest and admin-basic  authentication are stored in the [install-dir]/conf/admin.password file by default.
The none method allows all access.

After adding the module, restart your Wowza media server instance. 

You can access the module via the following URLs:

Reload schedule:
http://[wowza-address]:8086/scheduleloader?action=load&app=APP-NAME

Unload schedule:
http://[wowza-address]:8086/scheduleloader?action=unload&app=APP-NAME

## More resources
[How to schedule streaming with Wowza Streaming Engine (StreamPublisher)](https://www.wowza.com/docs/how-to-schedule-streaming-with-wowza-streaming-engine-streampublisher)

[How to create an HTTP Provider](https://www.wowza.com/docs/how-to-create-an-http-provider)

[Wowza Streaming Engine Java API](https://www.wowza.com/docs/wowza-streaming-engine-java-api-overview)

[How to extend Wowza Streaming Engine using the Wowza IDE](https://www.wowza.com/forums/content.php?759-How-to-extend-Wowza-Streaming-Engine-using-the-Wowza-IDE)

