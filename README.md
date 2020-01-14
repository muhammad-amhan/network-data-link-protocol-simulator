# Network Layers - Data Link Protocol Simulator
This project demonstrates how the `data link` layer in the network stack, works and behaves.
It demonstrates the process of sending and receiving data `frames` across the network.
This includes:

* Frame error detection 
* Constructing frames from a sent message (data)
* Message segmentation and construction from several frames received
* Handling and defining Maximum Transfer Unit (MTU)
* The `physical` and `application` layers are briefly introduced
* Frame structure: checksum, data length, delimiters etc.
* Handling frame noises and more...

## Getting Started
The project consists of two parts: 
1. Sender: the source where raw messages can be entered and frames will be constructed according to MTU and other settings

2. Receiver: the receiving node where you will need to copy the frames (one by one) outputted by the `Sender`, and paste them into the `Receiver` in the terminal to demonstrate how the message is reconstructed

### Terminology

* MTU: stands for maximum transfer unit, in other words, it will define the length of the `whole` frame (this varies based on the network cable, for example)

* Checksum: a number calculated using corresponding `ASCII` value for every character in the frame apart from the delimiters `< ... > `
* Data Length: a number calculated to define the length of the data (message) in the frame
* Frame Type: a character defined at the beginning of the frame to indicate whether the frame data is the final segment of the sent message - indicated with `E`, or the message is broken down into more frames indicated with `D`  
* Frame Delimiters: it's `<` and `>` which define the start and end of a single frame
* Field Delimiter: it's `-` which separates each of the above sections of the frame

#### Note:
The MTU value for both `Sender` and `Receiver` should be the same, because:

If a frame is copied from the `Sender` to be used in the `Receiver` but the `Receiver` has different `MTU` value, then it will detect an `MTU mismatch` 

Of course, any error or incorrect frame value e.g. copied the sent frames but manually changed the `checksum value`, `data length`, the `MTU` in the `Receiver` to the one that the `Sender` had when constructing the frames, will throw a `ProtocolException`.

### Prerequisites
`Java SE 8` should be fine, but I would recommend upgrading to `Java SE 11`


### Running Sender
The class `Senser.java` is in the following package and can run either via your IDE or command line as follow:
```
cd PROJECT_ROOT
java src.main.datalink.Sender [-m=, --mtu=<mtu value - defaut is 20>] [-d=, --debug=<true, false - default is true>]
```
When `Sender` runs, it will expect any raw input. It could be anything whatsoever (e.g. I'm the best at CS GO, I love computer science... 
*anything you see on your keyboard*). Let's use `hello` for simplicity:

```
hello

Output: <E-05-hello-37>
```
The `Sender` demonstrates how few things happen:
* The `frame type` is set to `E` because the `MTU = 20  (by default)` can handle the entire message in one frame because the entire frame does NOT exceed the MTU value, otherwise the message will be broken down 

* The `checksum` is calculates and set using the `ASCII` value for every character apart from the `frame delimiters` 
* The `message` is contained within the frame
* The `data length` is calculated and also fitted
* The `field delimiters` separate each section mentioned above

That's it.

If the `MTU = 12` and we tried to send `hello` again:
```
Output: <D-02-he-06>
        <D-02-ll-17>
        <E-01-o-12>
```
The frame itself, has the length `10` which leaves only `2` spaces for two characters/bytes

So the `MTU` doesn't have the ability to send the entire message in one frame. So it divides the message into three frames and uses `D` to tell the `Receiver` that there are more frames to build the message, until the last frame `E` to tell the `Receiver`, no more frames, you can build the message. 

### Running Receiver 
The class `Receiver.java` is in the following package, and can run either via your IDE or command line as follow:
```
cd PROJECT_ROOT
java src.main.datalink.Receiver [-m=, --mtu=<mtu value - defaut is 20>] [-d=, --debug=<true, false - default is true>]
```

When `Receiver` runs, it will expect a valid frame (e.g. frame type, valid format, checksum etc) such as:
```
<E-05-hello-37>
```

So, you can first run `Sender`, send a message and allow ot to construct frames then copy the frame into `Receiver` and it should output the same message you entered in `Sender`

The interesting part is setting the `Receiver`'s `MTU=12` as well, then send the same frames you got in `Sender` (one by one) and watch how `hello` will be constructed from received frames.  


## Acknowledgement
In collaboration with the University of Kent, School of Computing, this project was possible.
Special thanks to Mr. Peter Kenny in the School of Computing at Kent
