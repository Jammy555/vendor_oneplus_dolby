#
# SPDX-FileCopyrightText: The LineageOS Project
# SPDX-License-Identifier: Apache-2.0
#

# Inherit from the proprietary version
$(call inherit-product, vendor/sony/dolby/dolby-vendor.mk)

DOLBY_PATH := vendor/sony/dolby

# Configs
PRODUCT_COPY_FILES += \
    $(DOLBY_PATH)/configs/dolby/dax-default.xml:$(TARGET_COPY_OUT_VENDOR)/etc/dolby/dax-default.xml

# LunarisDolby
$(call inherit-product, packages/apps/LunarisDolby/lunarisdolby.mk)

# Overlay-RRO
PRODUCT_PACKAGES += \
    SonyDolbyResCommon

# Permissions
PRODUCT_COPY_FILES += \
    $(DOLBY_PATH)/configs/permissions/privapp-permissions-dolby.xml:$(TARGET_COPY_OUT_SYSTEM_EXT)/etc/permissions/privapp-permissions-dolby.xml

# Sepolicy
BOARD_VENDOR_SEPOLICY_DIRS += $(DOLBY_PATH)/sepolicy/vendor

# Properties
TARGET_VENDOR_PROP += $(DOLBY_PATH)/vendor.prop

# VINTF
DEVICE_FRAMEWORK_COMPATIBILITY_MATRIX_FILE += $(DOLBY_PATH)/configs/hidl/dolby_framework_matrix.xml
