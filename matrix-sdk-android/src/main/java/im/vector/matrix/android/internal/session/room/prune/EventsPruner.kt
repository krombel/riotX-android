package im.vector.matrix.android.internal.session.room.prune

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.zhuinden.monarchy.Monarchy
import im.vector.matrix.android.api.session.events.model.EventType
import im.vector.matrix.android.internal.database.RealmLiveEntityObserver
import im.vector.matrix.android.internal.database.mapper.asDomain
import im.vector.matrix.android.internal.database.model.EventEntity
import im.vector.matrix.android.internal.database.query.where
import im.vector.matrix.android.internal.util.WorkerParamsFactory

private const val PRUNE_EVENT_WORKER = "PRUNE_EVENT_WORKER"

internal class EventsPruner(monarchy: Monarchy) :
        RealmLiveEntityObserver<EventEntity>(monarchy) {

    override val query = Monarchy.Query<EventEntity> { EventEntity.where(it, type = EventType.REDACTION) }

    override fun process(inserted: List<EventEntity>, updated: List<EventEntity>, deleted: List<EventEntity>) {
        val redactionEvents = inserted.map { it.asDomain() }
        val pruneEventWorkerParams = PruneEventWorker.Params(redactionEvents)
        val workData = WorkerParamsFactory.toData(pruneEventWorkerParams)

        val sendWork = OneTimeWorkRequestBuilder<PruneEventWorker>()
                .setInputData(workData)
                .build()

        WorkManager.getInstance()
                .beginUniqueWork(PRUNE_EVENT_WORKER, ExistingWorkPolicy.APPEND, sendWork)
                .enqueue()
    }

}