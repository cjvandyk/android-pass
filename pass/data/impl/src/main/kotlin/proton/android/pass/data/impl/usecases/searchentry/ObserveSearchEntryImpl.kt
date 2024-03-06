/*
 * Copyright (c) 2023 Proton AG
 * This file is part of Proton AG and Proton Pass.
 *
 * Proton Pass is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Proton Pass is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Proton Pass.  If not, see <https://www.gnu.org/licenses/>.
 */

package proton.android.pass.data.impl.usecases.searchentry

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import proton.android.pass.data.api.SearchEntry
import proton.android.pass.data.api.repositories.SearchEntryRepository
import proton.android.pass.data.api.usecases.ObserveCurrentUser
import proton.android.pass.data.api.usecases.searchentry.ObserveSearchEntry
import javax.inject.Inject

class ObserveSearchEntryImpl @Inject constructor(
    private val observeCurrentUser: ObserveCurrentUser,
    private val searchEntryRepository: SearchEntryRepository
) : ObserveSearchEntry {

    override fun invoke(searchEntrySelection: ObserveSearchEntry.SearchEntrySelection): Flow<List<SearchEntry>> =
        when (searchEntrySelection) {
            ObserveSearchEntry.SearchEntrySelection.AllVaults -> observeCurrentUser()
                .flatMapLatest { searchEntryRepository.observeAll(it.userId) }
            is ObserveSearchEntry.SearchEntrySelection.Vault ->
                searchEntryRepository.observeAllByShare(searchEntrySelection.shareId)
        }
}
