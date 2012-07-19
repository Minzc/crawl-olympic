import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.http.client.ClientProtocolException;

public class CrawlerRunner {
	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("r", false, "craw R");
		options.addOption("e", false, "craw RE");
		options.addOption("w", true, "webset");
		options.addOption("t", true, "get json time");
		options.addOption("h", false, "Lists short help");
		
		int prd = 18000;
		CommandLineParser parser = new PosixParser();
		try {
			Timer tm = new Timer();
			myTask mytsk = new myTask();
			Crawler cr = new Crawler();

			CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption("h")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("craw", options);
				System.exit(0);
			} else if (cmd.hasOption("r") && !cmd.hasOption("e")) {
				mytsk.setCrawler(cr, "R");
			} else if (cmd.hasOption("e") && !cmd.hasOption("r")) {
				mytsk.setCrawler(cr, "RE");
			} else {
				System.err.println("Parameter Error");
				System.exit(1);
			}
			
			if(cmd.hasOption("t"))
				prd = Integer.parseInt(cmd.getOptionValue("t")) * 1000;
			System.out.println(prd);
			
			boolean rst = cr.login("cc777", "000000", cmd.getOptionValue("w"));
			if (rst == true)
				System.out.println("Login Success");
			else
				System.exit(1);

			tm.schedule(mytsk, 1000, prd);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

class myTask extends TimerTask {
	private Crawler cr;
	private String target;

	public void setCrawler(Crawler cr, String target) {
		this.cr = cr;
		this.target = target;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("MM_dd");
		String x = null;
		if (target == "R")
			x = cr.crawRJson();
		else if (target == "RE")
			x = cr.crawREJson();

		FileWriter fw;
		if(x.indexOf("<html>") != -1){
			try {
				cr.reLogin();
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else{
			try {
				fw = new FileWriter("output/" + target + "_"
						+ df.format(new Date()), true);
				fw.write(x + "\n\n");
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Json is " + x.substring(0, 40) + "...");
		}
	}

}
