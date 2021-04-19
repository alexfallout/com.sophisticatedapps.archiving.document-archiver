module DocumentArchiver {

    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.web;
    requires javafx.fxml;
    requires javafx.media;
    requires com.sun.jna;
    requires PDFViewerFX;
    requires com.jthemedetector;
    requires java.desktop;
    requires org.apache.commons.imaging;
    requires mammoth;
    requires org.apache.poi.scratchpad;
    requires Restart4j;

    opens com.sophisticatedapps.archiving.documentarchiver;
    opens com.sophisticatedapps.archiving.documentarchiver.controller;
    opens com.sophisticatedapps.archiving.documentarchiver.model;
    opens com.sophisticatedapps.archiving.documentarchiver.type;
    opens com.sophisticatedapps.archiving.documentarchiver.util;
    opens com.sophisticatedapps.archiving.documentarchiver.view;

    exports com.sophisticatedapps.archiving.documentarchiver to ArchiveBrowser;
    exports com.sophisticatedapps.archiving.documentarchiver.controller to ArchiveBrowser;
    exports com.sophisticatedapps.archiving.documentarchiver.model to ArchiveBrowser;
    exports com.sophisticatedapps.archiving.documentarchiver.type to ArchiveBrowser;
	exports com.sophisticatedapps.archiving.documentarchiver.util to ArchiveBrowser;
}
