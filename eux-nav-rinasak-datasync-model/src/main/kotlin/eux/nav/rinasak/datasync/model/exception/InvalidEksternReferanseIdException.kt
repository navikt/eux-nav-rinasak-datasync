package eux.nav.rinasak.datasync.model.exception

class InvalidEksternReferanseIdException(
    override val message: String?,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause)
