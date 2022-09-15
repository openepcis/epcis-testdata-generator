# Test Data Generator Examples

<div style="text-align:justify;">
To make it easier for the users to generate EPCIS test data events, the web application contains 3 views: Create Test Data, Design Test Data, and Generate Events.


### Create Test Data:
Users can quickly generate large amounts of test data events by using this view. When users merely require bulk EPCIS events and do not have a defined use case or supply chain, this view can be helpful. The Create Test Data view of the application is as shown in the following figure. In order to generate the events, users can input the required number of events as well as fill up various other EPCIS attribute information according to their preferences. This view only produces a specific kind of event. Hence, this is useful for users that immediately require a high number of events in their database for various testing purposes but do not care about the type of the events.

![Create Test Data View](./screenshots/Create%20Test%20Data%20View.png) 
<p align=center>Figure 1: Create Test Data View.</p>


### Create Test Data example:

In the [create test data ui](./create%20test%20data%20ui) folder, a sample example files have been provided that generates 10 ObjectEvents with various eventTime and readPoint values. Each event is composed of 10 unique Instance SGTIN identifiers and 5 unique Class LGTIN identifiers that were generated dynamically based on the supplied data. Using the "Import" button located in the top-right corner, this file may be 
loaded onto the Create Test Data page. In a similar fashion, users can easily construct hundreds or thousands of events depending on their own data and requirements. Within the [create test data ui](./create%20test%20data%20ui) folder, the "InputTemplate" and the created events in accordance with the supplied data have also been provided for reference.

### Design Test Data:
Every organization has a different use case and follows a particular set of supply chain stages, so each one needs the freedom to design its own supply chain and build EPCIS events based on it. As 
a result, the application's Design Test Data view has been created. Users can create their own supply chain systems using this view, provide different information dependent on the operation taking place on the objects, and generate a large number of events accordingly. The applications view with various events that can be utilized to create the supply chain is shown in the following screenshot.

![Design Test Data View](./screenshots/Design%20Test%20Data%20View.png)
<p align=center>Figure 2: Design Test Data View.</p>

The type of EPCIS events, Identifiers, and Parent Identifiers nodes that users can drag and drop to the canvas to build the supply chain are described in the left pane. To create the relation, all of these nodes can be joined together using the connector. The user can click the "Submit" button to produce the events after constructing the entire supply chain and giving all the nodes the necessary data.

### Design Test Data example:
Below is a simple example of creating a sequence of business processes and generating EPCIS events in accordance with it:

At first products in the supply chain are produced after the production, they are given specific SGTIN identifiers in ObjectEvent during the Commissioning business step. Later, using AggregationEvent with the packing business step, these products are packaged onto pallets. Then, using AggregationEvent with the loading business step, these pallets are loaded onto the containers. These containers are then transported to a specific location using ObjectEvent with the shipping business step for further commercial operations.

The supply chain system stated above is shown in the following figure, which was created using the Test Data Generator tool:

![Design Test Data Example](./screenshots/Design%20Test%20Data%20Example.png)
<p align=center>Figure 3: Design supply chain system example.</p>

The exported design for the aforementioned scenario is located in the [design test data ui](./design%20test%20data%20ui) folder, and it can be imported into the Design Test Data view using the Import option located in the 
top right corner. The [folder](design%20test%20data%20ui) includes the formatted InputTemplate JSON that was produced using the design and connections specified. Additionally, for reference, the folder includes the generated events in accordance with the design.

## Generate Events:
This is the application's output view, where the InputTemplate is displayed alongside the actual events or errors. The application generates the InputTemplate based on the design and data supplied 
by the user. The InputTemplate is sent to the backend service that creates the EPCIS events based on it. The sample InputTemplate file for the Create Test Data view and Design Test Data view can 
be found in the [generate events ui](./generate%20events%20ui) folder. Following image shows the glimps of Generate Events page:

![Generate Events](./screenshots/Generate%20Events.png)
<p align=center>Figure 4: Generate Events view.</p>

As seen in the above screenshot, the Textarea for InputTemplate and generated events both offer a variety of options, like copying the data and importing it from local storage or exporting it to 
local storage, among others.


</div>
