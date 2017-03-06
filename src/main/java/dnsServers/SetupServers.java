package dnsServers;

import java.io.IOException;

/**
 * Created by Shayan on 3/1/2017.
 */
public class SetupServers{

    public static int DNS_LISTEN_PORT = 1234;

    public static void main(String[] args) throws IOException {

        if (args[0].equals("root")) {
            DNS.RootDNS root = new DNS.RootDNS(DNS_LISTEN_PORT, "root", 7070);
            RunDns runDns1 = new RunDns(root);
            runDns1.start();
        }
        else if (args[0].equals("com")) {
            DNS.TldDNS com = new DNS.TldDNS(DNS_LISTEN_PORT, "com");
            RunDns runDns2 = new RunDns(com);
            runDns2.start();
        }
        else if (args[0].equals("google.com")) {
            DNS.AuthDNS google_com = new DNS.AuthDNS(DNS_LISTEN_PORT, "google.com");
            RunDns runDns4 = new RunDns(google_com);
            runDns4.start();
        }
        else if (args[0].equals("yahoo.com")) {
            DNS.AuthDNS yahoo_com = new DNS.AuthDNS(DNS_LISTEN_PORT, "yahoo.com");
            RunDns runDns5 = new RunDns(yahoo_com);
            runDns5.start();
        }







//        DNS.RootDNS secondRoot = new DNS.RootDNS(DNS_LISTEN_PORT, "secondRoot", 7070);
//
//        DNS.TldDNS org = new DNS.TldDNS(DNS_LISTEN_PORT, "org");
//
//        DNS.AuthDNS pbs_org = new DNS.AuthDNS(DNS_LISTEN_PORT, "pbs.org");
//
//
//        DNS.TldDNS ir = new DNS.TldDNS(DNS_LISTEN_PORT, "ir");
//
//        DNS.AuthDNS ac_ir = new DNS.AuthDNS(DNS_LISTEN_PORT, "ac.ir");
//        DNS.AuthDNS tehran_ir = new DNS.AuthDNS(DNS_LISTEN_PORT, "tehran.ir");










//
//
//        RunDns runDns8 = new RunDns(secondRoot);
//        runDns8.start();
//
//        RunDns runDns9 = new RunDns(org);
//        runDns9.start();
//
//        RunDns runDns10 = new RunDns(pbs_org);
//        runDns10.start();
//
//
//
//        RunDns runDns3 = new RunDns(ir);
//        runDns3.start();
//
//        RunDns runDns6 = new RunDns(ac_ir);
//        runDns6.start();
//
//        RunDns runDns7 = new RunDns(tehran_ir);
//        runDns7.start();

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
