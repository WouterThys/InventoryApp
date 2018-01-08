echo "Delete files in /opt/Inventory"
rm -rf /opt/Inventory/inventory.db
rm -rf /opt/Inventory/InventoryApp.jar
rm -rf /opt/Inventory/orderfiles
rm -rf /opt/Inventory/Images

echo "Copy .jar file to /opt/Inventory"
cp -ar /home/wouter/Documents/Geeken/InventoryApp/out/artifacts/InventoryApp_jar/InventoryApp.jar /opt/Inventory/

echo "Copy 'Images' to /opt/Inventory"
cp -ar /home/wouter/Documents/Geeken/InventoryApp/Images /opt/Inventory/

echo "Copy 'settings.db' to /opt/Inventory"
cp -ar /home/wouter/Documents/Geeken/InventoryApp/settings.db /opt/Inventory/

echo "Done copying local files"
echo "Trying to secure copy to laptop"
echo "zip inventory to Inventory.zip"

zip -r /opt/Inventory/Inventory.zip /opt/Inventory/* 
echo "try to scp"
scp /opt/Inventory/Inventory.zip wouter@192.168.0.250:~/Desktop

echo "Done!"
