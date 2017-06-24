echo "Delete files in /opt/Inventory"
rm -rf /opt/Inventory/inventory.db
rm -rf /opt/Inventory/InventoryApp.jar
rm -rf /opt/Inventory/orderfiles

echo "Copy .jar file to /opt/Inventory"
cp -ar /home/waldo/Documents/Geeken/Projects/InventoryApp/out/artifacts/InventoryApp_jar/InventoryApp.jar /opt/Inventory/

echo "Copy .db file to /opt/Inventory"
cp -ar /home/waldo/Documents/Geeken/Projects/InventoryApp/inventory.db /opt/Inventory/

echo "Copy 'orderfiles' to /opt/Inventory"
cp -ar /home/waldo/Documents/Geeken/Projects/InventoryApp/orderfiles /opt/Inventory/

echo "Copy 'Images' to /opt/Inventory"
cp -ar /home/waldo/Documents/Geeken/Projects/InventoryApp/Images /opt/Inventory/


echo "Done!"
