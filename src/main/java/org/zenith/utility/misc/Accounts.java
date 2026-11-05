package org.zenith.utility.misc;

import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import org.zenith.utility.api.JsonListStorage;
import org.zenith.utility.api.MicrosoftAuth;

public final class Accounts extends JsonListStorage<Account> {
   private static final String text = "zenith-accounts-v1";
   private User session;
   private int intValue = -1;
   private String status = "";

   public Accounts() {
      super("accounts.json", "zenith-accounts-v1", (new TypeToken<List<Account>>() {}).getType(), CopyOnWriteArrayList::new);
      this.items = new CopyOnWriteArrayList<>(this.items);
   }

   public User method4750() {
      if (this.session == null) {
         this.session = Minecraft.getInstance().getUser();
      }

      return this.session;
   }

   public List<Account> method4751() {
      return new ArrayList<>(this.items);
   }

   public int size() {
      return this.items.size();
   }

   public Account method4752() {
      var accounts = this.method4751();
      return this.intValue >= 0 && this.intValue < accounts.size() ? (Account)accounts.get(this.intValue) : null;
   }

   public void method4753(Account name) {
      if (name != null && name.getName() != null && !name.getName().isBlank()) {
         for (Account account : this.items) {
            if (account.getName().equalsIgnoreCase(name.getName()) && account.getType() == name.getType()) {
               if (name.method5473()) {
                  account.setText(name.getText());
                  account.setText2(name.getText2());
                  account.setText3(name.getText3());
                  this.save();
               }

               return;
            }
         }

         this.add(name);
         this.save();
      }
   }

   public void method4754(int n) {
      var accounts = this.method4751();
      if (n >= 0 && n < accounts.size()) {
         this.remove((Account)accounts.get(n));
         if (this.intValue == n) {
            this.intValue = -1;
         } else if (this.intValue > n) {
            this.intValue--;
         }

         this.save();
      }
   }

   public void select(int intValue) {
      var accounts = this.method4751();
      if (intValue >= 0 && intValue < accounts.size()) {
         Account account = (Account)accounts.get(intValue);
         if (!account.method5473()) {
            this.method4756(new User(account.getName(), account.method5472(), "", Optional.empty(), Optional.empty()));
            this.intValue = intValue;
            this.status = "Switched to " + account.getName();
         } else if (account.getText3() != null && !account.getText3().isEmpty()) {
            this.status = "Refreshing " + account.getName() + "...";
            MicrosoftAuth.method3059(account.getText3(), var1x -> this.status = var1x)
               .whenComplete((var2x, var3x) -> Minecraft.getInstance().execute(() -> {
                  if (var3x == null && var2x != null) {
                     account.setName(var2x.name());
                     account.setText(var2x.uuid().toString());
                     account.setText2(var2x.getText2());
                     account.setText3(var2x.getText3());
                     this.save();
                     this.method4756(new User(var2x.name(), var2x.uuid(), var2x.getText2(), Optional.empty(), Optional.empty()));
                     this.intValue = this.method4751().indexOf(account);
                     this.status = "Switched to " + var2x.name();
                  } else {
                     this.status = "Failed to refresh: " + rootMessage(var3x);
                  }
               }));
         } else {
            this.method4756(new User(account.getName(), account.method5472(), account.getText2(), Optional.empty(), Optional.empty()));
            this.intValue = intValue;
            this.status = "Switched to " + account.getName();
         }
      }
   }

   public void method4755() {
      User user = this.method4750();
      this.method4756(user);
      this.intValue = -1;
      this.status = "Restored " + user.getName();
   }

   private void method4756(User user) {
      Minecraft.getInstance().user = user;
   }

   public void method4757(String str) {
      this.status = str == null ? "" : str;
   }

   public static String rootMessage(Throwable throwable) {
      Throwable throwable2 = throwable;

      while (throwable2 != null && throwable2.getCause() != null) {
         throwable2 = throwable2.getCause();
      }

      if (throwable2 == null) {
         return "unknown error";
      } else {
         String message = throwable2.getMessage();
         return message != null && !message.isBlank() ? message : throwable2.getClass().getSimpleName();
      }
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public String getStatus() {
      return this.status;
   }
}
