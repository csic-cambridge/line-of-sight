# Line of Sight - PowerApps Application

![[Pasted image 20220321115308.png]]


## Key Features

* Sign-in and registration
* Project creation
* Objective category definition
* Organisational objective capture
* Creation of Organisational Information Requirements (OIR)
* Creation of Functional Informatino Requirements (FIR)
* Creation of Asset Information Requirements (AIR)
* Integration with Uniclass2015 EF Elements & Functions Table
* Integration with Uniclass 2015 SS Systems Table
* Summary page of information requirements
* Export tables to CSV format

## How To Install

To run this application, you'll need to install the .zip solution folder into your PowerApps environment. 

1.  Sign into [Power Apps](https://make.powerapps.com/?utm_source=padocs&utm_medium=linkinadoc&utm_campaign=referralsfromdoc) and select **Solutions** from the left navigation.
    
2.  On the command bar, select **Import**.
    
    ![Import solution.](https://docs.microsoft.com/en-us/powerapps/maker/data-platform/media/solution-import.png "Import solution")
    
3.  On the **Import a solution** page, select **Browse** to locate the compressed (.zip or .cab) file that contains the solution you want to import.
    
4.  Select **Next**.
    
5.  Information about the solution is displayed. By default, in the **Advanced settings** section, if SDK messages and flows exist in the solution, they will be imported. Clear the **Enable SDK messages and flows included in the solution** option if you want them to import in an inactive state.
    
6.  If your solution contains [connection references](https://docs.microsoft.com/en-us/powerapps/maker/data-platform/create-connection-reference), you’ll be prompted to select the connections you want. If a connection does not already exist, create a new one. Select **Next**.
    
7.  If your solution contains [environment variables](https://docs.microsoft.com/en-us/powerapps/maker/data-platform/environmentvariables), you will be prompted to enter values. You will not see this screen if value(s) are already present in your solution or the target environment.
    
8.  If missing dependencies are detected in the target environment, a list of the dependencies is presented. In environments where the required package version is available for import in the target environment, a link to resolve the dependency is presented. Selecting the link takes you to the Power Platform admin center where you can install the application update. After the application update is completed, you can start the solution import again.
    
9.  Select **Import**.

## Link to  PowerApps Installation Documentation
* [Import solutions - Power Apps | Microsoft Docs](https://docs.microsoft.com/en-us/powerapps/maker/data-platform/import-update-export-solutions#:~:text=To%20import%20a%20solution%3A%201%20Sign%20into%20Power,they%20will%20be%20imported.%20...%20More%20items...%20)

## Methodology
[line_of_sight_july_2021.pdf (cam.ac.uk)](https://www-smartinfrastructure.eng.cam.ac.uk/files/line_of_sight_july_2021.pdf)

---