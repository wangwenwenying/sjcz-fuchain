package com.flkj.config;

import cn.flt.Intermediate.FabricManager;
import cn.flt.Intermediate.OrgManager;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "fabric")
public class EnvConfigFabric implements CommandLineRunner {
    private static Logger log = LoggerFactory.getLogger(EnvConfigFabric.class);
    /*init*/
    private String cc;
    private String orgMSPID;
    private boolean openTLS;
    /*setUser*/
    private String userleagueName;
    private String userorgName;
    private String userpeerName;
    private String username;
    private String userskPath;
    private String usercertificatePath;
    /*addOrderer*/
    private String orderername;
    private String ordererlocation;
    private String ordererserverCrtPath;
    private String ordererclientCertPath;
    private String ordererclientKeyPath;
    /*addPeer*/
    private String peerName;
    private String peerLocation;
    private String peerEventHubLocation;
    private String peerserverCrtPath;
    private String peerclientCertPath;
    private String peerclientKeyPath;
    /*setChannel*/
    private String channelName;
    /*addChainCode*/
    private String chaincodeName;
    private String chaincodePath;
    private String GOPATH;
    private String chaincodePolicy;
    private String chaincodeVersion;
    private int proposalWaitTime;

    public FabricManager fm;

    public OrgManager orgManager;



    @Override
    public void run(String... args) {
        System.out.println(">>>>>>>>>>>>>>>affair服务启动执行，执行加载数据等操作<<<<<<<<<<<<<");
        OrgManager om = new OrgManager();
        try {
            om.init(getCc(),
                    getOrgMSPID(),
                    isOpenTLS());

            om.addOrderer(getOrderername(),
                    getOrdererlocation(),
                    getOrdererserverCrtPath(),
                    getOrdererclientCertPath(),
                    getOrdererclientKeyPath());

            om.addPeer(getPeerName(),
                    getPeerLocation(),
                    getPeerEventHubLocation(),
                    getPeerserverCrtPath(),
                    getPeerclientCertPath(),
                    getPeerclientKeyPath());

            om.setUser(getUserleagueName(),
                    getUserorgName(),
                    getUserpeerName(),
                    getUsername(),
                    getUserskPath(),
                    getUsercertificatePath());

            om.setChannel(getChannelName());

//            orgManager = om;


//            om.addChainCode(getChaincodeName(),
//                    getChaincodePath(),
//                    getGOPATH(),
//                    getChaincodePolicy(),
//                    getChaincodeName(),
//                    getProposalWaitTime());

            om.add();

            fm = om.use(getCc(),getUsername());
            System.out.println(fm.queryPeer());
            System.out.println(fm.getBlockchainInfo());
            System.out.println(fm.queryBlockByNumber(300));
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        log.info("fm init ok!");
    }
}
