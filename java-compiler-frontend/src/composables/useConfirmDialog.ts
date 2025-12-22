import { useConfirm } from 'primevue/useconfirm';

export function useConfirmDialog() {
    const confirm = useConfirm();

    const confirmDelete = (options: {
        header?: string;
        message?: string;
        acceptIcon?: string;
        onAccept: () => void;
        onReject?: () => void;
    }) => {
        confirm.require({
            group: 'headless',
            header: options.header,
            message: options.message,
            accept: options.onAccept,
            reject: options.onReject,
            acceptIcon: options.acceptIcon
        });
    };

    return { confirmDelete, confirm };
}
