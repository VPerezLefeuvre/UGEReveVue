export function StatusIndicator({ label, status }: { label: string; status?: string }) {
    const isUp = status === 'UP';
    return (
        <div className="flex items-center justify-between p-4 bg-white border rounded-lg shadow-sm">
            <span className="text-sm font-medium text-gray-600">{label}</span>
            <div className="flex items-center gap-2">
        <span className={`text-xs font-bold px-2 py-1 rounded ${isUp ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
          {status || 'UNKNOWN'}
        </span>
                <span className={`h-2 w-2 rounded-full ${isUp ? 'bg-green-500' : 'bg-red-500'} ${isUp && 'animate-pulse'}`} />
            </div>
        </div>
    );
}
