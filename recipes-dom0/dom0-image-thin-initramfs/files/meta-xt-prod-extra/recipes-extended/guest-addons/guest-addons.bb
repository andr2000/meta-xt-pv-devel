SUMMARY = "config files and scripts for a guest"
DESCRIPTION = "config files and scripts for guest which will be running for tests"

require inc/xt_shared_env.inc

PV = "0.1"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "\
    file://domd-salvator-x-m3.cfg \
    file://domd-salvator-x-h3.cfg \
    file://domu-salvator-x-m3.cfg \
    file://domu-salvator-x-h3.cfg \
    file://doma-salvator-x-m3.cfg \
    file://doma-salvator-x-h3.cfg \
    file://guest_domd \
    file://guest_domu \
    file://start_guest.sh \
    file://dom0_vcpu_pin.sh \
    file://xt_set_root_dev_cfg.sh \
"

S = "${WORKDIR}"

DOMD_CONFIG_salvator-x-m3-xt = "domd-salvator-x-m3.cfg"
DOMD_CONFIG_salvator-x-h3-xt = "domd-salvator-x-h3.cfg"
DOMA_CONFIG_salvator-x-m3-xt = "doma-salvator-x-m3.cfg"
DOMA_CONFIG_salvator-x-h3-xt = "doma-salvator-x-h3.cfg"
DOMU_CONFIG_salvator-x-m3-xt = "domu-salvator-x-m3.cfg"
DOMU_CONFIG_salvator-x-h3-xt = "domu-salvator-x-h3.cfg"

DOM0_ALLOWED_PCPUS_salvator-x-m3-xt = "2-5"
DOM0_ALLOWED_PCPUS_salvator-x-h3-xt = "4-7"

DOMU_ALLOWED_PCPUS_salvator-x-m3-xt = "2-5"
DOMU_ALLOWED_PCPUS_salvator-x-h3-xt = "4-7"

FILES_${PN} = " \
    ${base_prefix}${XT_DIR_ABS_ROOTFS_DOM_CFG}/*.cfg \
"

inherit update-rc.d

FILES_${PN}-run-domd += " \
    ${sysconfdir}/init.d/guest_domd \
"

FILES_${PN}-run-domu += " \
    ${sysconfdir}/init.d/guest_domu \
    ${base_prefix}${XT_DIR_ABS_ROOTFS_SCRIPTS}/start_guest.sh \
"

FILES_${PN}-run-vcpu_pin += " \
    ${sysconfdir}/init.d/dom0_vcpu_pin.sh \
"

FILES_${PN}-run-set_root_dev += " \
    ${sysconfdir}/init.d/xt_set_root_dev_cfg.sh \
"

PACKAGES += " \
    ${PN}-run-domd \
    ${PN}-run-domu \
    ${PN}-run-vcpu_pin \
    ${PN}-run-set_root_dev \
"

# configure init.d scripts
INITSCRIPT_PACKAGES = "${PN}-run-domd ${PN}-run-domu ${PN}-run-vcpu_pin ${PN}-run-set_root_dev"

INITSCRIPT_NAME_${PN}-run-domd = "guest_domd"
INITSCRIPT_PARAMS_${PN}-run-domd = "defaults 85"
INITSCRIPT_NAME_${PN}-run-domu = "guest_domu"
INITSCRIPT_PARAMS_${PN}-run-domu = "defaults 86"
INITSCRIPT_NAME_${PN}-run-vcpu_pin = "dom0_vcpu_pin.sh"
INITSCRIPT_PARAMS_${PN}-run-vcpu_pin = "defaults 81"
# must run before any domain creation
INITSCRIPT_NAME_${PN}-run-set_root_dev = "xt_set_root_dev_cfg.sh"
INITSCRIPT_PARAMS_${PN}-run-set_root_dev = "defaults 82"

do_install() {
    if [ -n "${DOMD_CONFIG}" ] ; then
        install -d ${D}${base_prefix}${XT_DIR_ABS_ROOTFS_DOM_CFG}
        install -m 0744 ${WORKDIR}/${DOMD_CONFIG} ${D}${base_prefix}${XT_DIR_ABS_ROOTFS_DOM_CFG}/domd.cfg
    fi
    if [ -n "${DOMU_CONFIG}" ] ; then
        install -d ${D}${base_prefix}${XT_DIR_ABS_ROOTFS_DOM_CFG}
        install -m 0744 ${WORKDIR}/${DOMU_CONFIG} ${D}${base_prefix}${XT_DIR_ABS_ROOTFS_DOM_CFG}/domu.cfg
        # Fixup a number of PCPUs the VCPUs of DomF must run on
        sed -i "s/DOMU_ALLOWED_PCPUS/${DOMU_ALLOWED_PCPUS}/g" ${D}${base_prefix}${XT_DIR_ABS_ROOTFS_DOM_CFG}/domu.cfg
    fi

    if [ -n "${DOMA_CONFIG}" ] ; then
        install -d ${D}${base_prefix}${XT_DIR_ABS_ROOTFS_DOM_CFG}
        install -m 0744 ${WORKDIR}/${DOMA_CONFIG} ${D}${base_prefix}${XT_DIR_ABS_ROOTFS_DOM_CFG}/doma.cfg
        # Fixup a number of PCPUs the VCPUs of DomF must run on
        sed -i "s/DOMU_ALLOWED_PCPUS/${DOMU_ALLOWED_PCPUS}/g" ${D}${base_prefix}${XT_DIR_ABS_ROOTFS_DOM_CFG}/doma.cfg
    fi

    install -d ${D}${base_prefix}${XT_DIR_ABS_ROOTFS_SCRIPTS}
    install -d ${D}${sysconfdir}/init.d
    install -m 0744 ${WORKDIR}/guest_domd ${D}${sysconfdir}/init.d/
    install -m 0744 ${WORKDIR}/guest_domu ${D}${sysconfdir}/init.d/
    install -m 0744 ${WORKDIR}/start_guest.sh ${D}${base_prefix}${XT_DIR_ABS_ROOTFS_SCRIPTS}/
    install -m 0744 ${WORKDIR}/dom0_vcpu_pin.sh ${D}${sysconfdir}/init.d/
    install -m 0744 ${WORKDIR}/xt_set_root_dev_cfg.sh ${D}${sysconfdir}/init.d/

    # Fixup a number of PCPUs the VCPUs of Dom0 must run on
    sed -i "s/DOM0_ALLOWED_PCPUS/${DOM0_ALLOWED_PCPUS}/g" ${D}${sysconfdir}/init.d/dom0_vcpu_pin.sh
}
