package fr.d0p1.hookscord;

import fr.d0p1.hookscord.utils.Message;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;

public class Main {
	
	static String user = "CrypticalNitrogen";
	static String pass = "36cxzasdewq";
	
	static List<Integer>  usersTradedWith= new ArrayList<Integer>();
	static List<String> limitedValues = new ArrayList<String>(); //name,value
	
	static String nft="White Sparkle Time Fedora"; //your items that are not for trade
	static String doNotGet="Real Ice Cold Stunnas, Interstellar Wings, Crimsonwrath, Lord of Entropy,Kleos Phóteinos,Aer Draco,Catching Snowflakes"; //items that you do not want to own
	
	
	static int maxProfitWhenUpgrading=2000; //the max the profit is when sending (people are more likely to accept when lower)
	static int leastProfit=200; //the least the profit is when sending a upgrade
	static int maxProfitWhenDowngrading=2000;
	static int leastProfitDowngrading=500; //the least the profit is when sending a downgrade 
	
	static JBrowserDriver driver = new JBrowserDriver(Settings.builder().blockAds(true).javascript(true).build()); //trading browser
	static JBrowserDriver driver2 = new JBrowserDriver(Settings.builder().blockAds(true).javascript(true).build()); //scraper browser
	
	static String webhook="";
	
	static String[] traders={};
	
	
	static boolean autoTrade=true;
	static String[] catIds={"1230403652","1428417334","1428409936","9254254","380754227","128992838","1016184756","658757624","110207471","628771505","91679217","130213380","362051899","323192138","1191129536","212971414","293316227","1301378557","323192138","399021751","1241224444","1563352","1301384400","1016184756","302281542","332772333","556821517","439946101","71484026","1191162013","835063865","564449640","151786902","583721561","119812738","1213472762","362081769","321570512","564449640","19027209","67996263","49763745","271015669","113325603","1563352","71484026","334656353","244160970","130213380","323191683","439945661","835095880","376806474","334656546","489170175","100425864","116043052","323191979"};

	static Scanner s = new Scanner(System.in); 
	
	public static void print(String text) throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime())+": "+ text);
		
		try {
			Hookscord hk2 = new Hookscord(webhook); 
			Message msg2 = new Message("PoppyBytes");
			if (text.contains("Sent [")) {
				msg2.setText("```"+text+"```");
			} else {
				msg2.setText(text);
			}
			hk2.sendMessage(msg2);
		} catch(MalformedURLException e) {
			
		}
	}
	
	public static String[] findTraderFromReseller() throws InterruptedException {
		Random rand = new Random();
		String[] data={"",""};
		driver2.get("https://www.roblox.com/catalog/"+catIds[rand.nextInt(catIds.length)]);
		driver2.pageWait();
		Thread.sleep(1000);
		
		try {
		WebElement reseller = (WebElement)driver2.findElements(By.cssSelector("li.list-item")).get(rand.nextInt(9));
		
		String name=reseller.findElement(By.cssSelector("a.text-name.username")).getText();
		String id=reseller.findElement(By.cssSelector("a.text-name.username")).getAttribute("href").replace("https://www.roblox.com/users/", "").replace("/profile", "");
		System.out.println(name+" : "+id);
		
		for (int i=0;i<usersTradedWith.size();i++) {
			if (usersTradedWith.get(i)== Integer.parseInt(id)) {
				String[] data1={"",""};
				return data1;
			}
		}
		
		data[0]=name;
		data[1]=id;
		
		}catch (Exception e) {
			
		}
	
		return data;
	}
	
	public static String[] findTraderFromRocks() {
		driver2.get("https://rbx.rocks/");
		driver2.pageWait();
		int id=Integer.parseInt(driver2.findElement(By.cssSelector("a#username_tag")).getAttribute("href").replace("https://roblox.com/users/", "").replace("/profile", "").trim());
		Random rand = new Random();
		String name=driver2.findElements(By.cssSelector("a#username_tag")).get(rand.nextInt(10)).getText();
		for (int i=0;i<usersTradedWith.size();i++) {
			if (usersTradedWith.get(i)== id) {
				String[] data={"",""};
				return data;
			}
		}
		
		String[] data = {name,new Integer(id).toString()};
		
		return data;
	}
	
	public static void sendTrade(String userToTrade,String userID) throws InterruptedException, IOException{
		
		JavascriptExecutor js = (JavascriptExecutor)driver;
		
		try {
			driver.get("https://www.roblox.com/home");
			driver.pageWait();
		} catch(Exception e) {
			
		}
		try {
		js.executeScript("window.location='https://www.roblox.com/Trade/TradeWindow.aspx?TradePartnerID="+userID+"';");
		Thread.sleep(4000);
		} catch(NoSuchElementException e) {
			return;
		}
		driver.pageWait();
		try {
			
			if (!driver.getPageSource().contains("Your Offer")) {
				if (userID.contains(":false")) {
					usersTradedWith.add(Integer.parseInt(userID));
					return;
				}
				print(userID+" does not have trade enabled.");
				usersTradedWith.add(Integer.parseInt(userID));
				return;
			}	
		} catch(Exception e) {
			return;
		}
		
		print("Started trade with "+userID);
		/* JS for inserting items into trade. 
		var searchText = ''; 
		var pageLimit=30;
		var offer=document.querySelector('div#ctl00_cphRoblox_InventoryControl1_InventoryContainer.InventoryContainer'); 
		var aTags = offer.querySelector('div.InventoryHandle').children; 
		
		if (offer.querySelector('span.paging_currentpage').textContent !='1') { 
			var goBack=parseInt(offer.querySelector('span.paging_currentpage').textContent); 
			for (var i1=0;i1<pageLimit;i1++) { 
				offer.querySelector('div.paging_previous').click(); 
			} 
		} 
		
		look(); 
		function look() { 
			for (var i = 0; i < aTags.length; i++) { 
				if (aTags[i].querySelector('div.InventoryNameWrapper').querySelector('a.InventoryItemLink').querySelector('div.InventoryItemName') !== undefined) {
					var txt=aTags[i].querySelector('div.InventoryNameWrapper').querySelector('a.InventoryItemLink').querySelector('div.InventoryItemName').textContent;
					console.log(txt);  
					if (txt.trim()===searchText.trim()) { 
						aTags[i].querySelector('div.ItemLinkDiv').querySelector('img.ItemImg').click(); 
						return false; 
					}
				} 
			} 
			
			if(pageLimit==0) { 
				return false; 
			} else { 
				offer.querySelector('div.paging_next').click(); 
				pageLimit--;
				look(); 
				return false; 
			} 
		}

		*/
		
		WebElement offer=(WebElement)driver.findElements(By.cssSelector("div#ctl00_cphRoblox_InventoryControl1_InventoryContainer.InventoryContainer")).get(0);
		
		WebElement request=(WebElement)driver.findElements(By.cssSelector("div#ctl00_cphRoblox_InventoryControl2_InventoryContainer.InventoryContainer")).get(0);
		
		
		List<String>  offerI= new ArrayList<String>();
		List<Integer>  offerIValue= new ArrayList<Integer>();
		List<String> requestI = new ArrayList<String>();
		List<Integer>  requestIValue= new ArrayList<Integer>();
		List<Integer>  requestNotValued= new ArrayList<Integer>();
		
		driver2.get("https://rbx.rocks/s/"+user);
		driver2.pageWait();
		Thread.sleep(4000);
		WebElement itemContainer=null;
		try {
			itemContainer=(WebElement)driver2.findElement(By.cssSelector("div#ItemsContainer.row.active"));
		} catch(Exception e5) {
			return;
		}
		
		int i=0;
		while (true) {
			try { 
				WebElement item=itemContainer.findElements(By.cssSelector("div.col.s12.m4.l4")).get(i);
				String itemName=item.findElements(By.cssSelector("a.truncate")).get(0).getText();
				int itemValue=Integer.parseInt(item.findElements(By.cssSelector("a.truncate")).get(2).getText().replaceAll(",", "").replace("Value: ", ""));
				int itemRap=Integer.parseInt(item.findElements(By.cssSelector("a.truncate")).get(1).getText().replaceAll(",", "").replace("RAP: ", ""));
				boolean isNFT=false;
				
				if (nft.contains(itemName)) {
					isNFT=true;
				}
				
				if (isNFT==false) {
					offerI.add(itemName);
					offerIValue.add(itemValue);
				}
				
			} catch (Exception e) {
				break;
			}
			i++;
		}
		
		driver2.get("https://rbx.rocks/s/"+userToTrade);
		driver2.pageWait();
		Thread.sleep(6000);
		WebElement itemContainer1=null;
		try{ 
			itemContainer1=(WebElement)driver2.findElement(By.cssSelector("div#ItemsContainer.row.active"));
		} catch(Exception e5) {
			return;
		}	
		int i1=0;
		while (true) {
		
			try { 
				WebElement item1=itemContainer1.findElements(By.cssSelector("div.col.s12.m4.l4")).get(i1);
				String itemName1=item1.findElements(By.cssSelector("a.truncate")).get(0).getText();
				int itemValue1=Integer.parseInt(item1.findElements(By.cssSelector("a.truncate")).get(2).getText().replaceAll(",", "").replace("Value: ", ""));
				int itemRap1=Integer.parseInt(item1.findElements(By.cssSelector("a.truncate")).get(1).getText().replaceAll(",", "").replace("RAP: ", ""));
				boolean dNotGet=false;
				
				if (itemValue1==itemRap1) {
					System.out.println("Value equals rap");
					requestNotValued.add(itemValue1);
				}
				
				
				if (doNotGet.contains(itemName1)|| itemName1.contains("'")) {
					dNotGet=true;
				}
				
				
				if (dNotGet==false) {
					requestI.add(itemName1);
					requestIValue.add(itemValue1);
				}
			} catch (Exception e) {
				break;
			}
			i1++;
		}
		
		
		if (offerI.size()==0) {
			print("Error: Not enough to trade with or couldn't load inventory.");
			return;
		}
		if (requestI.size()==0) {
			return;
		}
		
		int offerValue=0;
		int requestValue=0;
		
		for (int i4=0; i4<offerIValue.size();i4++) {
			offerValue+=offerIValue.get(i4);
		}
		
		for (int i4=0; i4<requestIValue.size();i4++) {
			requestValue+=requestIValue.get(i4);
		}
		
		System.out.println(requestValue);
		
		//calculate profitable trade 
		List<Integer>  offerItems= new ArrayList<Integer>();
		List<Integer>  requestItems= new ArrayList<Integer>();
		
		for (int i4=0; i4<requestIValue.size();i4++) {
			int upgradeValue=requestIValue.get(i4);
			if (upgradeValue <= offerValue+maxProfitWhenUpgrading) {
				
				int upgradeLeastProfit=leastProfit;
				int upgradeMaxProfit=maxProfitWhenUpgrading;
				
				for (int y=0;y<requestNotValued.size();y++) {
					if (upgradeValue==requestNotValued.get(y)) {
						System.out.println("Not valued");
						upgradeLeastProfit=(int) (leastProfit*1.1);
						upgradeMaxProfit=(int)(maxProfitWhenUpgrading*1.5);
						break;
					}
				}
				
				
				if (upgradeValue < 2900) {
					upgradeLeastProfit=-40;
					upgradeMaxProfit=maxProfitWhenUpgrading/4;
				} else {
				}
				
				int firstItem=-1;
				int secondItem=-1;
				int thirdItem=-1;
				int fourthItem=-1;
				
				for (int i5=0; i5<offerIValue.size();i5++) {
					if (offerIValue.get(i5) <= upgradeValue-upgradeLeastProfit) {
						firstItem=i5;
					}
				}
				
				if (firstItem==-1) {
					continue;
				}
				
				for (int i5=0; i5<offerIValue.size();i5++) {
					if (offerIValue.get(firstItem) + offerIValue.get(i5) <= upgradeValue-upgradeLeastProfit && i5!=firstItem) {
						secondItem=i5;
					}
				}
				
				if (secondItem==-1) {
					continue;
				}
				
				if (upgradeValue-(offerIValue.get(firstItem) + offerIValue.get(secondItem)) <= upgradeMaxProfit && upgradeValue-(offerIValue.get(firstItem) + offerIValue.get(secondItem)) >= upgradeLeastProfit) {
					offerItems.add(firstItem);
					offerItems.add(secondItem);
					requestItems.add(i4);
					break;
				}
				
				for (int i5=0; i5<offerIValue.size();i5++) {
					if (offerIValue.get(firstItem) + offerIValue.get(secondItem) + offerIValue.get(i5) <= upgradeValue-upgradeLeastProfit && i5!=firstItem && i5!=secondItem) {
						thirdItem=i5;
					}
				}
				
				if (thirdItem==-1) {
					continue;
				}
				
				if (upgradeValue-(offerIValue.get(firstItem) + offerIValue.get(secondItem)+offerIValue.get(thirdItem)) <= upgradeMaxProfit && upgradeValue-(offerIValue.get(firstItem) + offerIValue.get(thirdItem)+offerIValue.get(secondItem)) >= upgradeLeastProfit) {
					offerItems.add(firstItem);
					offerItems.add(secondItem);
					offerItems.add(thirdItem);
					requestItems.add(i4);
					break;
				}
				
				for (int i5=0; i5<offerIValue.size();i5++) {
					if (offerIValue.get(firstItem) + offerIValue.get(secondItem) + offerIValue.get(thirdItem) + offerIValue.get(i5) < upgradeValue-upgradeLeastProfit && i5!=firstItem && i5!=secondItem &&  i5!=thirdItem) {
						fourthItem=i5;
					}
				}
				
				if (fourthItem==-1) {
					continue;
				}
				
				if (upgradeValue-(offerIValue.get(firstItem) + offerIValue.get(secondItem)+offerIValue.get(thirdItem)+offerIValue.get(fourthItem)) <= upgradeMaxProfit && upgradeValue-(offerIValue.get(firstItem) + offerIValue.get(thirdItem)+offerIValue.get(secondItem)+offerIValue.get(fourthItem)) >= upgradeLeastProfit) {

					offerItems.add(firstItem);
					offerItems.add(secondItem);
					offerItems.add(thirdItem);
					offerItems.add(fourthItem);
					requestItems.add(i4);
					break;
				}

			} else {
				continue;
			}
		}
		
		
		List<String>  offerNames= new ArrayList<String>();
		List<String>  requestNames= new ArrayList<String>();
		
		for (int i6=0;i6<offerItems.size();i6++) {
			offerNames.add(offerI.get(offerItems.get(i6)));
		}
		
		
		if (offerItems.size()==0) {
			for (int i4=0; i4<offerIValue.size();i4++) {
				int upgradeValue=offerIValue.get(i4);
				if (upgradeValue>1500) {
					
					int firstItem=-1;
					int secondItem=-1;
					int thirdItem=-1;
					int fourthItem=-1;
					
					for (int i5=requestIValue.size()-1; i5>0;i5--) {
						if (requestIValue.get(i5) <= upgradeValue) {
						if (requestIValue.get(i5)!=upgradeValue) {	
							firstItem=i5;
						}	
						}
					}
					
					if (firstItem==-1) {
						continue;
					}
					
					for (int i5=0; i5<requestIValue.size();i5++) {
						if (upgradeValue-(requestIValue.get(firstItem) + requestIValue.get(i5)) >= -maxProfitWhenDowngrading && upgradeValue-(requestIValue.get(firstItem) + requestIValue.get(i5)) <= -leastProfitDowngrading) {
							if (requestIValue.get(i5)!=upgradeValue) {
								secondItem=i5;
							}
						}
					}
					
					if (secondItem==-1) {
						continue;
					}
			
					System.out.println(upgradeValue+" - "+(requestIValue.get(firstItem)+" +  " + requestIValue.get(secondItem)));
					if (upgradeValue-(requestIValue.get(firstItem) + requestIValue.get(secondItem)) >= -maxProfitWhenDowngrading && upgradeValue-(requestIValue.get(firstItem) + requestIValue.get(secondItem)) <= -leastProfitDowngrading) {
						requestItems.add(firstItem);
						requestItems.add(secondItem);
						offerItems.add(i4);
						System.out.println("H");
						break;
					}
					
					for (int i5=0; i5<requestIValue.size();i5++) {
						if (upgradeValue-(requestIValue.get(firstItem) + requestIValue.get(secondItem)+ requestIValue.get(i5)) >= -maxProfitWhenDowngrading && upgradeValue-(requestIValue.get(firstItem) + requestIValue.get(secondItem)+requestIValue.get(i5)) <= -leastProfitDowngrading) {
							thirdItem=i5;
						}
					}
					
					if (thirdItem==-1) {
						continue;
					}
					System.out.println(upgradeValue+" - "+(requestIValue.get(firstItem)+" +  " + requestIValue.get(secondItem))+" + "+requestIValue.get(thirdItem));
					if (upgradeValue-(requestIValue.get(firstItem) + requestIValue.get(secondItem)+requestIValue.get(thirdItem)) >= -maxProfitWhenDowngrading && upgradeValue-(requestIValue.get(firstItem) + requestIValue.get(thirdItem)+requestIValue.get(secondItem)) <= -leastProfitDowngrading) {
						requestItems.add(firstItem);
						requestItems.add(secondItem);
						requestItems.add(thirdItem);
						System.out.println("H");
						offerItems.add(i4);
						break;
					}
					
					for (int i5=0; i5<requestIValue.size();i5++) {
						if (upgradeValue-(requestIValue.get(firstItem) + requestIValue.get(secondItem)+requestIValue.get(thirdItem)+ requestIValue.get(i5)) >= -maxProfitWhenDowngrading && upgradeValue-(requestIValue.get(firstItem) +requestIValue.get(thirdItem)+requestIValue.get(secondItem)+requestIValue.get(i5)) <= -leastProfitDowngrading) {
							fourthItem=i5;
						}
					}
					
					if (fourthItem==-1) {
						continue;
					}
					
					System.out.println(upgradeValue-(requestIValue.get(firstItem) + requestIValue.get(secondItem)+ requestIValue.get(thirdItem)+requestIValue.get(fourthItem)));
					if (upgradeValue-(requestIValue.get(firstItem) + requestIValue.get(secondItem)+ requestIValue.get(thirdItem)+requestIValue.get(fourthItem)) >= -maxProfitWhenDowngrading && upgradeValue-(requestIValue.get(firstItem) + requestIValue.get(thirdItem)+requestIValue.get(secondItem)+requestIValue.get(fourthItem)) <= -leastProfitDowngrading) {
						requestItems.add(firstItem);
						requestItems.add(secondItem);
						requestItems.add(thirdItem);
						requestItems.add(fourthItem);
						System.out.println("H");
						offerItems.add(i4);
						break;
					}

				} else {
					//
				}
			}
			
			System.out.println("H1");
			
			for (int i6=0;i6<requestItems.size();i6++) {
				requestNames.add(requestI.get(requestItems.get(i6)));
			}
			
				if (offerItems.size()!=0) {
					System.out.println("H2");
					try {
						usersTradedWith.add(Integer.parseInt(userID));
					} catch(Exception e) {
						
					}
					String output="Sent ["+offerI.get(offerItems.get(0))+"] for " + requestNames+"".toString();
					
					
					try { 
						for (int i8=0;i8<10;i8++) {
							Thread.sleep(50);
							for (int i7=0;i7<requestNames.size();i7++) {
								System.out.println(requestNames.get(i7));
								js.executeScript("var searchText = '"+requestNames.get(i7)+"'; var pageLimit=30; var offer=document.querySelector('div#ctl00_cphRoblox_InventoryControl2_InventoryContainer.InventoryContainer'); var aTags = offer.querySelector('div.InventoryHandle').children; if (offer.querySelector('span.paging_currentpage').textContent !='1') { var goBack=parseInt(offer.querySelector('span.paging_currentpage').textContent); for (var i1=0;i1<pageLimit;i1++) { offer.querySelector('div.paging_previous').click(); } } look(); function look() { for (var i = 0; i < aTags.length; i++) { if (aTags[i].querySelector('div.InventoryNameWrapper').querySelector('a.InventoryItemLink').querySelector('div.InventoryItemName') !== undefined) { var txt=aTags[i].querySelector('div.InventoryNameWrapper').querySelector('a.InventoryItemLink').querySelector('div.InventoryItemName').textContent; console.log(txt); if (txt.trim()===searchText.trim()) { console.log('G'); aTags[i].querySelector('div.ItemLinkDiv').querySelector('img.ItemImg').click(); return false; } } } if(pageLimit==0) { return false; } else { offer.querySelector('div.paging_next').click(); pageLimit--; look(); return false; } }");
								Thread.sleep(100);
							}
							Thread.sleep(50);
							WebElement offerSElement=driver.findElements(By.cssSelector("select.InventorySmall.CategoryDropDown")).get(1);
							offerSElement.click();
						
							WebElement oElement=offerSElement.findElements(By.tagName("option")).get(0);
							oElement.click();
						}
					} catch(Exception e) {
						print("Failed to send");
						return;
					}	
			
					try { 
						for (int i8=0;i8<10;i8++) {
							Thread.sleep(50);
							js.executeScript("var searchText = '"+offerI.get(offerItems.get(0))+"'; var pageLimit=30; var offer=document.querySelector('div#ctl00_cphRoblox_InventoryControl1_InventoryContainer.InventoryContainer'); var aTags = offer.querySelector('div.InventoryHandle').children; if (offer.querySelector('span.paging_currentpage').textContent !='1') { var goBack=parseInt(offer.querySelector('span.paging_currentpage').textContent); for (var i1=0;i1<pageLimit;i1++) { offer.querySelector('div.paging_previous').click(); } } look(); function look() { for (var i = 0; i < aTags.length; i++) { if (aTags[i].querySelector('div.InventoryNameWrapper').querySelector('a.InventoryItemLink').querySelector('div.InventoryItemName') !== undefined) { var txt=aTags[i].querySelector('div.InventoryNameWrapper').querySelector('a.InventoryItemLink').querySelector('div.InventoryItemName').textContent; console.log(txt); if (txt.trim()===searchText.trim()) { console.log('G'); aTags[i].querySelector('div.ItemLinkDiv').querySelector('img.ItemImg').click(); return false; } } } if(pageLimit==0) { return false; } else { offer.querySelector('div.paging_next').click(); pageLimit--; look(); return false; } }");
							Thread.sleep(100);
							WebElement reqSElement=driver.findElements(By.cssSelector("select.InventorySmall.CategoryDropDown")).get(0);
							reqSElement.click();
						
							WebElement oElement=reqSElement.findElements(By.tagName("option")).get(0);
							oElement.click();
						}
					} catch(Exception e) {
						print("Failed to send");
						return;
					}
				
					
					WebElement offerThing=driver.findElements(By.cssSelector("div.OfferItems")).get(1);
					int numInRequest=0;
					
					for (int x1=0;x1<5;x1++) {
						try {
							WebElement itemInRequest=offerThing.findElements(By.cssSelector("div.InventoryItemContainerOuter")).get(x1);
							numInRequest=numInRequest+1;
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
					
					System.out.println(numInRequest);
					
					if (numInRequest==requestNames.size()) {
						System.out.println("H4");
						try{
							js.executeScript("document.getElementsByClassName('SendTrade')[0].click();"); //sendButton
							Thread.sleep(1000);
							js.executeScript("document.querySelector('a#roblox-confirm-btn.btn-neutral.btn-large').click();");
							print(output);
							
							Thread.sleep(3000);
						}catch(Exception e) {
							System.err.println("Failed to send.");
							return;
						}
						
						try {
							usersTradedWith.add(Integer.parseInt(userID));
						} catch(Exception e) {
							
						}
					}
				
				} else {
					try {
						usersTradedWith.add(Integer.parseInt(userID));
					} catch(Exception e) {
						
					}
					print("Could not calculate a profitable trade.");
				}
			
		} else {
			String output="Sent "+offerNames+" for [" + requestI.get(requestItems.get(0))+"]".toString();
				
				//upgrading
				try { 
					for (int i8=0;i8<10;i8++) {
						Thread.sleep(50);
						for (int i7=0;i7<offerNames.size();i7++) {
							js.executeScript("var searchText = '"+offerNames.get(i7)+"'; var pageLimit=31; var offer=document.querySelector('div#ctl00_cphRoblox_InventoryControl1_InventoryContainer.InventoryContainer'); var aTags = offer.querySelector('div.InventoryHandle').children; if (offer.querySelector('span.paging_currentpage').textContent !='1') { var goBack=parseInt(offer.querySelector('span.paging_currentpage').textContent); for (var i1=0;i1<pageLimit;i1++) { offer.querySelector('div.paging_previous').click(); } } look(); function look() { for (var i = 0; i < aTags.length; i++) { if (aTags[i].querySelector('div.InventoryNameWrapper').querySelector('a.InventoryItemLink').querySelector('div.InventoryItemName') !== undefined) { var txt=aTags[i].querySelector('div.InventoryNameWrapper').querySelector('a.InventoryItemLink').querySelector('div.InventoryItemName').textContent; console.log(txt); if (txt.trim().replace(/ +(?= )/g,'')===searchText.trim()) { console.log('G'); aTags[i].querySelector('div.ItemLinkDiv').querySelector('img.ItemImg').click(); return false; } } } if(pageLimit==0) { return false; } else { offer.querySelector('div.paging_next').click(); pageLimit--; look(); return false; } }");
							Thread.sleep(500);
						}
						Thread.sleep(50);
						WebElement offerSElement=driver.findElements(By.cssSelector("select.InventorySmall.CategoryDropDown")).get(0);
						offerSElement.click();
						
						WebElement oElement=offerSElement.findElements(By.tagName("option")).get(0);
						oElement.click();
					}
				} catch(Exception e) {
					System.err.println("Failed to send");
					return;
				}
			
			
				try { 
					for (int i8=0;i8<10;i8++) {
						Thread.sleep(100);
						js.executeScript("var searchText = '"+requestI.get(requestItems.get(0))+"'; var pageLimit=31; var offer=document.querySelector('div#ctl00_cphRoblox_InventoryControl2_InventoryContainer.InventoryContainer'); var aTags = offer.querySelector('div.InventoryHandle').children; if (offer.querySelector('span.paging_currentpage').textContent !='1') { var goBack=parseInt(offer.querySelector('span.paging_currentpage').textContent); for (var i1=0;i1<pageLimit;i1++) { offer.querySelector('div.paging_previous').click(); } } look(); function look() { for (var i = 0; i < aTags.length; i++) { if (aTags[i].querySelector('div.InventoryNameWrapper').querySelector('a.InventoryItemLink').querySelector('div.InventoryItemName') !== undefined) { var txt=aTags[i].querySelector('div.InventoryNameWrapper').querySelector('a.InventoryItemLink').querySelector('div.InventoryItemName').textContent; console.log(txt); if (txt.trim().replace(/ +(?= )/g,'')===searchText.trim()) { console.log('G'); aTags[i].querySelector('div.ItemLinkDiv').querySelector('img.ItemImg').click(); return false; } } } if(pageLimit==0) { return false; } else { offer.querySelector('div.paging_next').click(); pageLimit--; look(); return false; } }");
						Thread.sleep(100);
						WebElement reqSElement=driver.findElements(By.cssSelector("select.InventorySmall.CategoryDropDown")).get(1);
						reqSElement.click();
						
						WebElement oElement=reqSElement.findElements(By.tagName("option")).get(0);
						oElement.click();
					}
				} catch(Exception e) {
					System.err.println("Failed to send");
					return;
				}
					
				try{
					js.executeScript("document.getElementsByClassName('SendTrade')[0].click();"); //sendButton
					Thread.sleep(1000);
					js.executeScript("document.querySelector('a#roblox-confirm-btn.btn-neutral.btn-large').click();");
					
					print(output);
					
					Thread.sleep(3000);
					
				}catch(Exception e) {
					e.printStackTrace();
					System.err.println("Failed to send.");
					return;
				}
			
				try {
					usersTradedWith.add(Integer.parseInt(userID));
				} catch(Exception e) {
					
				}
	
		}
	}
	
	public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {
		
		File f=new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		
		String fi=f.getParentFile().getPath();
		fi=fi+"/config.properties"; 
		System.out.println(fi);
		File configFile=new File(fi);
		
		if(!configFile.exists()) {
		     print("Error: Could not find config file. Make sure it is in the same directory as the program and named config.");
		     System.exit(0);
		}
		
		try {
			InputStream input = new FileInputStream(configFile);
			Properties prop = new Properties();
			prop.load(input);
			
			user = prop.getProperty("username");
			pass = prop.getProperty("password");
			
			nft=prop.getProperty("itemsNotForTrade");
			doNotGet=prop.getProperty("itemsNotToOwn");
			maxProfitWhenUpgrading=Integer.parseInt(prop.getProperty("maxProfitWhenUpgrading").trim());
			leastProfit=Integer.parseInt(prop.getProperty("leastProfitWhenUpgrading").trim());
			maxProfitWhenDowngrading=Integer.parseInt(prop.getProperty("maxProfitWhenDowngrading").trim());
			leastProfitDowngrading=Integer.parseInt(prop.getProperty("leastProfitWhenDowngrading").trim());
			
			//autoTrade=Boolean.parseBoolean(prop.getProperty("autoTrade"));
			webhook=prop.getProperty("discordWebHook");
			
		}catch(Exception e) {
			print("Error: Could not load config file.");
			System.exit(0);
		}
		
		driver.get("https://pastebin.com/wPVuCgAd");
		driver.pageWait();
		if (!driver.getPageSource().contains(user)) {
			System.exit(0);
		} 
		
		try {
			driver.get("https://roblox.com");
			driver.pageWait();
	        
		}catch(Exception e) {
				
		}	
			print("Account whitelisted.");
			print("Logging In..");
			WebElement UsernameEl = (WebElement)driver.findElements(By.cssSelector("input#LoginUsername")).get(0);
			WebElement PasswordEl = (WebElement)driver.findElements(By.cssSelector("input#LoginPassword")).get(0);
			UsernameEl.sendKeys(user);
			PasswordEl.sendKeys(pass);
			WebElement LoginBtnEl = driver.findElement(By.cssSelector("input#LoginButton"));
			LoginBtnEl.click();
			driver.pageWait();
        
        if (driver.getPageSource().contains("Enter the code we just sent to you via email.")) { //2fa
        	print("Please disable 2fa or enter code.");
        	System.out.println("Enter 2-Step Verification Code:");
        	Scanner sc=new Scanner(System.in); 
        	String twoStepCode=sc.next(); 
        	WebElement twoStepEl = (WebElement)driver.findElements(By.name("verification-code")).get(0);
        	twoStepEl.click();
        	twoStepEl.sendKeys(twoStepCode);
        	WebElement twoBtnEl = (WebElement)driver.findElements(By.cssSelector("button.btn-primary-md")).get(0);
        	print("Using code: "+twoStepEl.getAttribute("value"));
        	twoBtnEl.click();
        	driver.pageWait();
        	Thread.sleep(6000);
        	if (driver.manage().getCookies().toString().contains(".ROBLOSECURITY")) {
        		print("Login Success.");	
        	} else if(driver.getPageSource().contains("Invalid code.")) {
        		print("Error: Invalid code.");
        		System.exit(0);
        	} else {
        		print("Error: Page timeout.");
        		System.exit(0);
        	}
		} else if(driver.getPageSource().contains(user)) { //no 2fa
        	print("Login Success.");
        } else {
        	print("Login Failed.");
        	System.exit(0);
        	
        }
        
        driver2.get("https://api.roblox.com/users/get-by-username?username="+user);
        driver2.pageWait();
        String jsonId=driver2.findElement(By.tagName("pre")).getText();
        String id = jsonId.substring(6,jsonId.indexOf(",")).trim();
      
       
       driver.get("https://www.roblox.com/users/"+id+"/profile");
       
       boolean bc=false;
       
       try {
    	   WebElement BC = (WebElement)driver.findElement(By.cssSelector("span.icon-bc"));
    	   bc=true;
       } catch(Exception e) {
       } 
       try {
    	   WebElement BC = (WebElement)driver.findElement(By.cssSelector("span.icon-obc"));
    	   bc=true;
       } catch(Exception e) {
       }  
       try {
    	   WebElement BC = (WebElement)driver.findElement(By.cssSelector("span.icon-tbc"));
    	   bc=true;
       } catch(Exception e) {
       }         
              
       
       
       if (bc==false) {
    	   print("Error: You don't have BC.");
    	   System.exit(0);
       }
       
      	//int x=0; Code for manually inserting usernames to trade
 		//while (x < traders.length) {
  			//String trader =traders[x];
  	        //driver2.get("https://api.roblox.com/users/get-by-username?username="+trader);
  	       // driver2.pageWait();
  	       // String jsonId1=driver2.findElement(By.tagName("pre")).getText();
  	       // String id1 = jsonId1.substring(6,jsonId1.indexOf(",")).trim();
			//sendTrade(trader,id1);
			//Thread.sleep(500);
			//x++;
  		//}
     
    	   while (user==user) {
    		   	Random rand = new Random();
    		   	
    		   	String[] trader={"",""};
    		   	
    		   	int r=rand.nextInt(3);
    		   	
    		   	if (r==2) {
    		   		trader=findTraderFromRocks();
    		   	} else {
    		   		trader=findTraderFromReseller();
    		   	}
   			
   				if (trader[0].equals("")) {
   					continue;
   				}
   				sendTrade(trader[0],trader[1]);
   				Thread.sleep(500);
   			}	
	}
}


