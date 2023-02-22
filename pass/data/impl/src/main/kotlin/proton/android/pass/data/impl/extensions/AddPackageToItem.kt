package proton.android.pass.data.impl.extensions

import proton.pass.domain.entity.PackageInfo
import proton.pass.domain.entity.PackageName
import proton_pass_item_v1.ItemV1

fun ItemV1.Item.hasPackageName(packageName: PackageName): Boolean =
    platformSpecific.android.allowedAppsList.any { it.packageName == packageName.value }

fun ItemV1.Item.with(packageInfo: PackageInfo): ItemV1.Item {
    val allowedApps = platformSpecific.android.allowedAppsList.toMutableList()
    allowedApps.add(
        ItemV1.AllowedAndroidApp.newBuilder()
            .setAppName(packageInfo.appName.value)
            .setPackageName(packageInfo.packageName.value)
            .build()
    )

    return this.toBuilder()
        .setPlatformSpecific(
            platformSpecific.toBuilder()
                .setAndroid(
                    platformSpecific.android.toBuilder()
                        .clearAllowedApps()
                        .addAllAllowedApps(allowedApps)
                        .build()
                )
                .build()
        )
        .build()
}

fun ItemV1.Item.withUrl(url: String): ItemV1.Item {
    val websites = content.login.urlsList.toMutableList()
    websites.add(url)
    return this.toBuilder()
        .setContent(
            content.toBuilder()
                .setLogin(
                    content.login.toBuilder()
                        .addUrls(url)
                        .build()
                )
                .build()
        )
        .build()
}
