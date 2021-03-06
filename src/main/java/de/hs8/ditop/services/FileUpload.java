package de.hs8.ditop.services;

import de.hs8.ditop.datastructures.DiTopDatasets;
import de.hs8.ditop.helper.Unzip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by hen on 10/28/14.
 */

@Controller
@RequestMapping("/ditop")
public class FileUpload {

    private final DiTopDatasets dataSets;

    @Autowired
    public FileUpload(DiTopDatasets dataSets) {
        this.dataSets = dataSets;
    }


    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }


    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(@RequestParam("file") MultipartFile file){
        String filename = file.getOriginalFilename();

        if (!file.isEmpty()) {
            try {


                byte[] bytes = file.getBytes();
                File storeFile = new File(dataSets.getDataDir()+ filename );
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(storeFile));
                stream.write(bytes);
                stream.close();

                if (filename.endsWith(".zip")) {
                    Unzip.unzipFile(storeFile.getAbsolutePath(), dataSets.getDataDir());
                    storeFile.delete();

                    dataSets.recreateConfigFile();

                }


                return "You successfully uploaded " + filename +". Please go back and hit reload !"
                        +"<script>\n" + "function goBack()\n" + "  {\n"
                        + "  window.history.back()\n" + "  }\n"
                        + "</script>\n" + "\n"
                        + "<body> "
                        + "<button onclick=\"goBack()\">Go Back</button>\n"
                        + "</body>";
            } catch (Exception e) {
                return "You failed to upload " + filename + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + filename + " because the file was empty.";
        }
    }

}
