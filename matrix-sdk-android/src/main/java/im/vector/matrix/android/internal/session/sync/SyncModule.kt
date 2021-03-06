package im.vector.matrix.android.internal.session.sync

import im.vector.matrix.android.internal.session.DefaultSession
import im.vector.matrix.android.internal.session.sync.job.SyncThread
import org.koin.dsl.module.module
import retrofit2.Retrofit


internal class SyncModule {

    val definition = module(override = true) {

        scope(DefaultSession.SCOPE) {
            val retrofit: Retrofit = get()
            retrofit.create(SyncAPI::class.java)
        }

        scope(DefaultSession.SCOPE) {
            ReadReceiptHandler()
        }

        scope(DefaultSession.SCOPE) {
            RoomSyncHandler(get(), get())
        }

        scope(DefaultSession.SCOPE) {
            GroupSyncHandler(get())
        }

        scope(DefaultSession.SCOPE) {
            UserAccountDataSyncHandler(get())
        }

        scope(DefaultSession.SCOPE) {
            SyncResponseHandler(get(), get(), get())
        }

        scope(DefaultSession.SCOPE) {
            DefaultSyncTask(get(), get()) as SyncTask
        }

        scope(DefaultSession.SCOPE) {
            SyncTokenStore(get())
        }

        scope(DefaultSession.SCOPE) {
            SyncThread(get(), get(), get(), get(), get())
        }

    }
}
