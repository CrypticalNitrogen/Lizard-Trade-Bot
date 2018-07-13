# Hookscord
Just a simple java api for discord webhooks

## Usage

```java
Hookscord hk = new Hookscord("https://discordapp.com/api/webhooks/467277224593195009/WXrsSUrqBtx460z84weGsblWirnHFFvTodnz1FF47pLChP1anKxqVvMVP3g99P03Pa5z");
Message msg = new Message("Hi Master!");
msg.setText("Cryptical");
hk.sendMessage(msg);
```

## Thanks to:

* [Dakurei](https://twitter.com/Dakurei_PVT) : name and charset fix
