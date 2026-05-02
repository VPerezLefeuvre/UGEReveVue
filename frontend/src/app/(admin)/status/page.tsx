import { getBackendHealth } from '@/core/api/health-api';
import { StatusIndicator } from '@/components/StatusIndicator';

export default async function StatusPage() {
    const health = await getBackendHealth();

    return (
        <div className="p-8 max-w-2xl mx-auto">
            <h1 className="text-2xl font-bold mb-6 text-gray-800">System Monitoring</h1>

            <div className="grid gap-4">
                <StatusIndicator label="Core Backend" status={health.status} />
                <StatusIndicator label="PostgreSQL Database" status={health.components?.db?.status} />
            </div>
        </div>
    );
}
