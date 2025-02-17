默认情况下，Android 系统中同一应用的所有组件均运行在相同的进程和线程（称为主线程）中，新启动的应用组件会创建进程或者在已存在的进程中启动并使用相同的执行线程。 但是，也可以安排应用中的组件在单独的进程中运行，并为任何进程创建额外的线程

## **一、进程**
如果需要控制某个组件所属的进程，则可在清单文件中执行以下操作

各类组件标签：
  - **< activity >**
  - **< service >**
  - **< receiver >**
  - **< provider >**

  - **android:process** 属性，此属性可以指定该组件应在哪个进程运行，通过此属性可以使每个组件均在各自的进程中运行，或者使一些组件共享一个进程，而其他组件则不共享。 此外，还可以通过该属性使**不同应用**的组件在相同的进程中运行，但前提是这些应用共享相同的 Linux 用户 ID 并使用相同的证书进行签署

此外，**< application >** 元素也支持 **android:process** 属性，以设置适用于所有组件的默认值

如果内存不足而系统又需要内存时，系统可能会在某一时刻关闭某一进程，Android 系统将权衡所有进程对用户的相对重要程度来决定终止哪个进程，被终止的进程中运行的应用组件也会随之销毁， 当这些组件需要再次运行时，系统将为它们重启进程

## **二、进程的重要性**
Android 系统会尽量长时间地保持应用进程，但为了新建进程或运行更重要的进程，有时候就需要移除旧进程以回收内存。 为了确定保留或终止哪些进程，系统会根据进程中正在运行的组件以及这些组件的状态，将每个进程放入**"重要性层次结构"**中。 必要时，系统会首先消除重要性最低的进程，然后是重要性略逊的进程，依此类推

重要性层次结构一共分为五级，第一级最为重要

- 前台进程。即用户当前操作所必需的进程。通常，在任意给定时间前台进程都为数不多，只有在内存不足以支持它们同时继续运行这一情况下系统才会终止它们。 此时，设备往往已达到内存分页状态，因此需要终止一些前台进程来确保用户界面正常响应。如果一个进程满足以下任一条件，即视为前台进程：
  - 托管用户正在交互的 Activity（已调用 Activity 的 onResume() 方法）
  - 托管某个 Service，该Service绑定到用户正在交互的 Activity
  - 托管正在前台运行的 Service（服务已调用 startForeground()）
  - 托管正在执行生命周期回调的 Service（onCreate()、onStart() 或 onDestroy()）
  - 托管正执行其 onReceive() 方法的 BroadcastReceiver

- 可见进程。没有任何前台组件，但仍会影响用户在屏幕上所见内容的进程。可见进程被视为是极其重要的进程，除非为了维持所有前台进程同时运行而必须终止，否则系统不会终止这些进程。 如果一个进程满足以下任一条件，即视为可见进程：
  - 托管不在前台、但仍对用户可见的 Activity（已调用其 onPause() 方法）。例如，如果前台 Activity 启动了一个覆盖一部分屏幕的对话框时，就会出现这种情况
  - 托管绑定到可见或前台Activity 的 Service

- 服务进程。正在运行已使用 **startService()** 方法启动的服务且不属于上述两个更高类别的进程。尽管服务进程与用户所见内容没有直接关联，但是它们通常在执行一些用户关心的操作（例如，在后台播放音乐或从网络下载数据）。因此，除非内存不足以维持所有前台进程和可见进程同时运行，否则系统会让服务进程保持运行状态

- 后台进程。包含目前对用户不可见的 Activity 的进程（已调用 Activity 的 onStop() 方法）。这些进程对用户体验没有直接影响，系统可能会随时终止它们以回收内存供前台进程、可见进程或服务进程使用。 通常会有很多后台进程在运行，因此它们会保存在 LRU （最近最少使用）列表中，以确保包含用户最近查看的 Activity 的进程最后一个被终止。如果某个 Activity 正确实现了生命周期方法，并保存了其当前状态，则终止其进程不会对用户体验产生明显影响，因为当用户导航回该 Activity 时，Activity 会恢复其所有可见状态

- 空进程。不含任何活动应用组件的进程。保留这种进程的的唯一目的是用作缓存，以缩短下次在其中运行组件所需的启动时间。 为使总体系统资源在进程缓存和底层内核缓存之间保持平衡，系统往往会终止这些进程

根据进程中当前活动组件的重要程度，Android 会将进程评定为它可以达到的最高级别。例如，如果某进程托管着服务和可见 Activity，则会将此进程评定为可见进程。此外，一个进程的级别可能会因其他进程对它的依赖而有所提高，即服务于另一进程的进程其级别永远不会低于其所服务的进程。 例如，如果进程 A 中的内容提供程序为进程 B 中的客户端提供服务，或者如果进程 A 中的服务绑定到进程 B 中的组件，则进程 A 始终被视为至少与进程 B 同样重要。

由于运行 Service 的进程其级别高于托管后台 Activity 的进程，因此启动长时间运行操作的 Activity 最好为该操作启动Service，而不是简单地创建工作线程，当操作有可能比 Activity 更加持久时尤要如此。例如，正在将图片上传到网站的 Activity 应该启动Service来执行上传操作，这样一来，即使用户退出 Activity，仍可在后台继续执行上传操作。使用Service可以保证，无论 Activity 发生什么情况，该操作至少具备“服务进程”优先级。 同理，广播接收器也应使用Service，而不是简单地将耗时冗长的操作放入线程中

## **三、线程**
应用启动时，系统会为应用创建一个名为"主线程"的执行线程。 此线程非常重要，因为它负责将事件分派给相应的用户界面小部件，其中包括绘图事件。此外，它也是应用与 Android UI 工具包组件（来自 `android.widget` 和 `android.view` 软件包的组件）进行交互的线程。因此，主线程也称为 UI 线程

系统不会为每个组件实例创建单独的线程。运行于同一进程的所有组件均在 UI 线程中实例化，并且对每个组件的系统调用均由该线程进行分派。 因此，响应系统回调的方法（比如报告用户操作的 onKeyDown() 或生命周期回调方法）始终在进程的 UI 线程中运行

例如，当用户触摸屏幕上的按钮时，应用的 UI 线程会将触摸事件分派给小部件，而小部件反过来又设置其按下状态，并将失效请求发布到事件队列中。 UI 线程从队列中取消该请求并通知小部件应该重绘自身

如果 UI 线程需要处理所有任务，则执行耗时很长的操作将会阻塞整个 UI。 一旦线程被阻塞，将无法分派任何事件，包括绘图事件。如果 UI 线程被阻塞超过特定时间（目前大约是 5 秒钟），用户就会看到一个显示"应用无响应"(ANR) 文本的对话框

此外，Android UI 工具包并非线程安全工具包。因此不能通过工作线程操纵 UI，而只能通过 UI 线程操纵用户界面。 因此，Android 的单线程模式必须遵守两条规则：

 - 不要阻塞 UI 线程
 - 不要在 UI 线程之外访问 Android UI 工具包. (页面在部分条件可有例外)

## **四、工作线程**
根据上述单线程模式，要保证应用 UI 的响应能力，关键是不能阻塞 UI 线程。如果执行的操作不能很快完成，则应确保它们在单独的线程中运行

例如，以下代码演示了一个点击侦听器从单独的线程下载图像并将其显示在 ImageView 中：

``` java
public void onClick(View v) {
    new Thread(new Runnable() {
        public void run() {
            Bitmap b = loadImageFromNetwork("http://example.com/image.png");
            mImageView.setImageBitmap(b);
        }
    }).start();
}
```
可以看出它创建了一个新线程来处理网络操作，但是它违反了单线程模式的第二条规则：不要在 UI 线程之外访问 Android UI 工具包。在工作线程中修改了 ImageView， 这将使应用运行时出现错误

为解决此问题，Android 提供了几种途径来从其他线程访问 UI 线程：

 - Activity.runOnUiThread(Runnable)
 - View.post(Runnable)
 - View.postDelayed(Runnable, long)

可以将以上代码修改为

``` java
public void onClick(View v) {
    new Thread(new Runnable() {
        public void run() {
            final Bitmap bitmap = loadImageFromNetwork("http://example.com/image.png");
            mImageView.post(new Runnable() {
                public void run() {
                    mImageView.setImageBitmap(bitmap);
                }
            });
        }
    }).start();
}
```
现在，上述实现属于线程安全型：在单独的线程中完成网络操作，而在 UI 线程中操纵 ImageView

此外，也可以使用AsyncTask来完成任务。AsyncTask 允许对用户界面执行异步操作。它会先阻塞工作线程中的操作，然后在 UI 线程中发布结果

要使用 AsyncTask ，必须创建 AsyncTask 的子类并实现 **doInBackground()** 回调方法，该方法将在后台线程池中运行。 要更新 UI，应该实现 **onPostExecute()** 方法以传递 **doInBackground()** 返回的结果并在 UI 线程中运行，以便安全地更新 UI

``` java
public void onClick(View v) {
    new DownloadImageTask().execute("http://example.com/image.png");
}

private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
 
    protected Bitmap doInBackground(String... urls) {
        return loadImageFromNetwork(urls[0]);
    }

    protected void onPostExecute(Bitmap result) {
        mImageView.setImageBitmap(result);
    }
	
}
```

AsyncTask 工作方法如下所示：

 - 使用泛型指定参数类型、进度值和任务最终值

 - doInBackground() 方法会在工作线程上自动执行

 - onPreExecute()、onPostExecute() 和 onProgressUpdate() 均在 UI 线程中调用

 - doInBackground() 返回的值将发送到 onPostExecute()

 - 可以随时在 doInBackground() 中调用publishProgress()方法，以便在 UI 线程中执行 onProgressUpdate()

 - 可以随时取消任何线程中的任务

   

### **这里提供本系列文章所有的 IPC 示例代码：[IPCSamples](https://github.com/leavesC/IPCSamples)**
