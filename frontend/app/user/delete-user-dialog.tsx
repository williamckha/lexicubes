import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "~/components/ui/dialog";
import { VisuallyHidden } from "@radix-ui/react-visually-hidden";
import React, { useState } from "react";
import { useDeleteUserMutation } from "~/user/user-queries";
import { Button } from "~/components/ui/button";

export function DeleteUserDialog({
  children,
  open,
  onOpenChange,
  ...props
}: React.ComponentProps<typeof Dialog>) {
  const {
    mutate: deleteUser,
    isPending,
    isSuccess,
    reset,
  } = useDeleteUserMutation(() => onDeleteUserSuccess());

  const isDialogDisabled = isPending || isSuccess;

  const [deleteConfirmationText, setDeleteConfirmationText] = useState("");
  const isDeleteButtonEnabled = deleteConfirmationText === "delete";

  const onDeleteUserSuccess = () => {
    reset();
    setDeleteConfirmationText("");
    onOpenChange?.(false);
  };

  return (
    <Dialog open={open || isDialogDisabled} onOpenChange={onOpenChange} {...props}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Delete account</DialogTitle>
          <VisuallyHidden>
            <DialogDescription>Delete your account</DialogDescription>
          </VisuallyHidden>
        </DialogHeader>
        <p>
          Are you sure you want to permanently delete this account? You will lose all history and
          stats associated with this account.
        </p>
        <div className="flex items-center gap-3">
          <p>Type "delete" to confirm:</p>
          <input
            className="flex-1 border-b-1 focus:border-b-2 transition-[border] py-1 outline-none  "
            type="text"
            placeholder="delete"
            value={deleteConfirmationText}
            onChange={(e) => setDeleteConfirmationText(e.target.value)}
            disabled={isDialogDisabled}
          />
        </div>
        <Button
          onClick={() => deleteUser()}
          disabled={!isDeleteButtonEnabled || isDialogDisabled}
          className="bg-destructive/10 hover:enabled:!bg-destructive/15 border-destructive text-destructive"
        >
          Delete account
        </Button>
      </DialogContent>
    </Dialog>
  );
}
