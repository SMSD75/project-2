package dnsServers;

import java.io.IOException;

/**
 * Created by Shayan on 3/1/2017.
 */
public class SetupServers{

    public static void main(String[] args) throws IOException {
        DNS.RootDNS root = new DNS.RootDNS(6060, "root", 7070);
//        root.addToDataBase("com", "6061");
//        root.addToDataBase("net", "6061");

        DNS.TldDNS com = new DNS.TldDNS(6061, "com");

        DNS.AuthDNS google_com = new DNS.AuthDNS(6063, "google.com");
        DNS.AuthDNS yahoo_com = new DNS.AuthDNS(6064, "yahoo.com");


        DNS.RootDNS secondRoot = new DNS.RootDNS(6070, "secondRoot", 7070);

        DNS.TldDNS org = new DNS.TldDNS(6071, "org");

        DNS.AuthDNS pbs_org = new DNS.AuthDNS(6072, "pbs.org");


        DNS.TldDNS ir = new DNS.TldDNS(6062, "ir");

        DNS.AuthDNS ac_ir = new DNS.AuthDNS(6065, "ac.ir");
        DNS.AuthDNS tehran_ir = new DNS.AuthDNS(6066, "tehran.ir");


        RunDns runDns1 = new RunDns(root);
        runDns1.start();

        RunDns runDns2 = new RunDns(com);
        runDns2.start();

        RunDns runDns4 = new RunDns(google_com);
        runDns4.start();

        RunDns runDns5 = new RunDns(yahoo_com);
        runDns5.start();



        RunDns runDns8 = new RunDns(secondRoot);
        runDns8.start();

        RunDns runDns9 = new RunDns(org);
        runDns9.start();

        RunDns runDns10 = new RunDns(pbs_org);
        runDns10.start();



        RunDns runDns3 = new RunDns(ir);
        runDns3.start();

        RunDns runDns6 = new RunDns(ac_ir);
        runDns6.start();

        RunDns runDns7 = new RunDns(tehran_ir);
        runDns7.start();

    }

    public static class RunDns extends Thread{

        DNS.RootDNS root = null;
        DNS.TldDNS tld = null;
        DNS.AuthDNS auth = null;

        public RunDns(DNS.RootDNS dns) {
            this.root = dns;
        }

        public RunDns(DNS.TldDNS dns) {
            this.tld = dns;
        }

        public RunDns(DNS.AuthDNS dns) {
            this.auth = dns;
        }

        public void run() {
            if (root != null)
                root.run();
            else if (tld != null)
                tld.run();
            else if (auth != null)
                auth.run();
        }
    }
}
