## Field service Application ##

### Debug keystore ###

In order anyone to be able to release new version to without issues the debug keystore should be common for everyone.
To do that the debug keystore is added to the project repo and a signing config is created to use that for signing debug and hockey app builds.

Common problems with the debug keystore: 
 - Map is not rendering. This might be another issue so it should be verified in the Logcat. 
 - Cannot update the application. This happens when the user have the old application with keystore A and try to update the application with
 keystore B. This is permitted by the system.

### VPN connection setup ###
In order to connect to the backend test environment your phone needs to be connected to VPN, unless you are on internal network.
To Connect to VPN when you are not in the internal network you need to input the following configuration you need a to download a client app - FortiClientVPN
For specific configuration contact an IT support or ask a colleague

### Application flavors and Build Variants ###
Due to diversity of the build variants the following section is brief explanation of them. Note that there are ignored build variants.
To see which build variants are ignored checked the build.gradle file in presentation module.

###### Application flavors ######
The since there are 3 apps and 3 environments, which can be connected in anyway they are divided into flavors with different dimensions - app, env
1. App dimension is used to specify the type of the app that is build: 
    - hem is the normal version of the app used by the engineers
    - natasha is the demo version of the app that contains no restrictions on the developed features. used for presenting features to clients.
    - helpDesk is the helpdesk version of the app used by the support.

2. Env dimension is used to specify the backend environment: 
    - jtest is the backend test environment. 
    - jint is the development environment.
    - prod is the production environment.

###### Build Types ######
There are 2 build types: 
  - debug. Used for development. Not obfuscated, debuggable. Not optimized.
  - release - used for distribution. Obfuscated and optimized. Not debuggable.

Build variants are build by combining the dimensions and the build type. Following combinations are currently in use:
 - hemJintDebug - main build variant for development
 - hemJintRelease - used for releasing version to HockeyApp(AppCenter)
 - hemJtestDebug - used for testing on the backend test environment
 - hemProdDebug - used for testing on production environment without the need of obfuscation
 - hemProdRelease - used for uploading to Google play store
 - helpdeskProdDebug - used for testing helpdesk version of the app
 - helpdeskProdRelease - used for uploading helpdesk version of the app to HockeyApp(AppCenter)
 - natashaProdDebug - used for testing natasha version of the app on production environment
 - natashaProdRelease - used for uploading natasha version of the app to HockeyApp(AppCenter)

**Important Note:** The Google maps for HemProdRelease version is  using different API code, than other release versions.
There are 2 Google Maps Api keys -> debug and release one. Debug one is used for all versions even the ones for AppCenter. 
The release key is used only by HemProdRelease build variant.

###### Change Log ######
There are 2 Changelogs: 
 1. Debug - Used for all versions for development and testing. 
 2. Release - Used only in production version of the app -> helpdeskProd, hemProd, natashaProd

### AppCenter ###
Uploading to App center is done manually. There are no prepared scripts.
When uploading new version don't forget to upload the mapping file of that version.

Here is more information on [Distribution on new app version](https://docs.microsoft.com/en-us/appcenter/sdk/distribute/android)

Versions in App center
  - Development version is used by developers and testers for testing specific features, that don't fall into the normal feature test flow.
  It can be used for PoC features as well. There are no limitations on what you want to upload. For this version the app id has to be manually set
  - Normal version is used by testers to test new features and bug fixes
  - Helpdesk version is used by support 
  - Demo(Natasha) is used for demo purposes

**Important** - For in-app updates there are some [limitations](https://docs.microsoft.com/en-us/appcenter/distribution/inappupdates).
 Foreach new version that is released an email is received. For the version that in-app updates are required the first Installation **MUST** happen via the email link.
 After that the application will be able to update via in-app updates. The first time you open the app (after you login) you will be automatically redirected to browser's app center.
  If you've never logged into appcenter via mobile browser, first time you do that you will be prompted to do so. After you login you should see a notice the in app updates were enabled for your account.

**Important** - the versions with build type originates from `hemProd` have their AppCenter setup disabled.

Useful information for testers -> [here](https://docs.microsoft.com/en-us/appcenter/distribution/testers/testing-android)

### ProGuard And Obfuscation ###
**NOTE:** There is a new option provided by android team for shrinking and obfuscating - R8. Currently this is not final. In Android Studio 3.3 this is enabled by default. [Details](https://r8.googlesource.com/r8)

The app is using Proguard for optimizing and obfuscating the code. [Details](https://developer.android.com/studio/build/shrink-code) 
This process takes time and is only enabled for Release builds (This includes the builds in the HockeyApp) in order to have faster build time.

#### Obfuscating and optimizing the code ####

You can find everything you need in the [ProGuard Manual](https://www.guardsquare.com/en/products/proguard/manual)
or [Android Developers Page](https://developer.android.com/studio/build/shrink-code) 
[This](https://jebware.com/blog/?p=418) is also useful to understand how the keep options are working.

The setup of the proguard is explained in [Android Developers Page](https://developer.android.com/studio/build/shrink-code).
This section is more focused on explaining how everything is organized in the project.
Application module (presentation) have the main proguard rules file - proguard-rules.pro. In that file are set all of the rules for proguard related to the application
For different libraries and dependencies we have separate rules files located in proguard folder in presentation module.
If new dependencies is added that needs to keep something it should be in that folder.

Other section of proguard-rules.pro is used for everything that doesn't fit anywhere

When this process is completed it generate few txt files in the presentation module build folder
location: ~/presentation/build/output/mapping/
Here is brief explanation of important files:

    1. mapping.txt:
     Translation and mapping between original and obfuscated classes. VERY IMPORTANT.
     This file should be kept and uploaded to store or hockey app in order to have unobfuscated stacktraces of the reported crashes.
     Each build generates new mapping file - so always keep mapping files and APKs together, because mapping file for APK v1 will not work correctly
     with mapping file for APK v2
    2. seeds.txt:  
     This is a list of all members that wasn't obfuscated. Useful to check if a rule you have written worked correctly.
    3. usage.txt: 
        This will give you a list of code that was removed from the APK during the shrinking phase. Useful to see if something was removed but you need to keep it. 

#### Manual DeObfuscating ####
If you have code you can manually map the stacktrace with the mapping.txt file you have 2 options to do that.
To you use both options you need to navigate to the android sdk folder and there to locate proguard tool folder - <Android-SDK>/tools/proguard/bin  

    1. Via Gui  - use proguard proguardgui.bat. Select "ReTrace" option and add your stacktrace and mapping file. Hit ReTrace! button.
    2. Via CLI  - have your stacktrace in a txt file. Then run following command: retrace.bat -verbose mapping.txt stacktrace.txt > out.txt

More details check following links:
[Android Developers Page](https://developer.android.com/studio/build/shrink-code) 
[Some useful link](https://coderwall.com/p/htq67g/android-how-to-decode-proguard-s-obfuscated-stack-trace)


   