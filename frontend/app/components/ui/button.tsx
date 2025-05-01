import React from "react";

export function Button({ className, ...props }: React.ComponentProps<"button">) {
  return (
    <button
      className={
        "flex flex-row justify-center items-center gap-2 w-full border-2 p-2 rounded-full font-medium " +
        "bg-background hover:enabled:bg-muted enabled:cursor-pointer disabled:cursor-not-allowed disabled:opacity-25 " +
        className
      }
      {...props}
    />
  );
}
