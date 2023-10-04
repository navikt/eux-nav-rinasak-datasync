package eux.nav.rinasak.datasync.service

import eux.nav.rinasak.datasync.integration.navrinasak.NavRinasakClient
import org.springframework.stereotype.Service

@Service
class NavRinasakSyncService(
    val navRinasakClient: NavRinasakClient
) {

}
