IMAGE_INSTALL_append = " \
    agl-users \
    pulseaudio \
    alsa-utils \
    wayland-ivi-extension \
    weston-init \
    packagegroup-xt-core-guest-addons \
    packagegroup-xt-core-xen \
    packagegroup-xt-core-pv \
    packagegroup-xt-core-network \
    kmscube \
    cldemo \
"

# Configuration for ARM Trusted Firmware
EXTRA_IMAGEDEPENDS += " arm-trusted-firmware"

# u-boot
DEPENDS += " u-boot"

# Dom0 is a generic ARMv8 machine w/o machine overrides,
# but still needs to know which system we are building,
# e.g. Salvator-X M3 or H3, for instance
# So, we provide machine overrides from this build to Dom0
EXTRA_IMAGEDEPENDS += " domd-install-machine-overrides"

# Do not support secure environment
IMAGE_INSTALL_remove = " \
    optee-linuxdriver \
    optee-linuxdriver-armtz \
    optee-client \
"

CORE_IMAGE_BASE_INSTALL_remove += "gtk+3-demo clutter-1.0-examples"

populate_vmlinux () {
    find ${STAGING_KERNEL_BUILDDIR} -iname "vmlinux*" -exec mv {} ${DEPLOY_DIR_IMAGE} \; || true
}

IMAGE_POSTPROCESS_COMMAND += "populate_vmlinux; "

