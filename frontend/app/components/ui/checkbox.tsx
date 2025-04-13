import * as React from "react";
import { useId, useState } from "react";
import * as CheckboxPrimitive from "@radix-ui/react-checkbox";
import { CheckIcon } from "lucide-react";

import { cn } from "~/lib/utils";
import { BiHelpCircle } from "react-icons/bi";

function Checkbox({ className, ...props }: React.ComponentProps<typeof CheckboxPrimitive.Root>) {
  return (
    <CheckboxPrimitive.Root
      data-slot="checkbox"
      className={cn(
        "peer border-input dark:bg-input/30 data-[state=checked]:bg-primary data-[state=checked]:text-primary-foreground dark:data-[state=checked]:bg-primary data-[state=checked]:border-primary focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive size-4 shrink-0 border-2 transition-shadow outline-none focus-visible:ring-[3px] disabled:cursor-not-allowed disabled:opacity-50",
        className,
      )}
      {...props}
    >
      <CheckboxPrimitive.Indicator
        data-slot="checkbox-indicator"
        className="flex items-center justify-center text-current transition-none"
      >
        <CheckIcon className="size-3.5" />
      </CheckboxPrimitive.Indicator>
    </CheckboxPrimitive.Root>
  );
}

interface CheckboxWithLabelProps extends React.ComponentProps<typeof CheckboxPrimitive.Root> {
  label: string;
  description?: string;
}

function CheckboxWithLabel({ className, label, description, ...props }: CheckboxWithLabelProps) {
  const id = useId();
  const [isDescriptionVisible, setIsDescriptionVisible] = useState(false);

  return (
    <div className="items-top flex space-x-2">
      <Checkbox id={id} className={className} {...props} />
      <div className="grid gap-1.5 leading-none">
        <label className="flex items-center gap-2" htmlFor={id}>
          {label}
          {description && (
            <button
              className="cursor-pointer"
              onClick={() => setIsDescriptionVisible(!isDescriptionVisible)}
            >
              <BiHelpCircle />
            </button>
          )}
        </label>
        {description && isDescriptionVisible && (
          <p className="text-sm text-muted-foreground">{description}</p>
        )}
      </div>
    </div>
  );
}

export { Checkbox, CheckboxWithLabel };
