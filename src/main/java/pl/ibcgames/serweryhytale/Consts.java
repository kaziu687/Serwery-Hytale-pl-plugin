package pl.ibcgames.serweryhytale;

import com.hypixel.hytale.server.core.Message;

import java.awt.*;

public final class Consts {

    // Komunikaty ogolne
    public static final Message CONFIG_INVALID = Message.raw("Plugin nie jest poprawnie skonfigurowany!").color(Color.red);
    public static final Message ERROR_GENERIC = Message.raw("Wystapil blad. Sprobuj pozniej.").color(Color.red);
    public static final Message PLAYER_NEEDED = Message.raw("Wykonaj komende jako gracz.").color(Color.red);

    // GlosujCommand
    public static final Message GLOSUJ_LOADING = Message.raw("Trwa pobieranie danych...").color(Color.green);
    public static final Message GLOSUJ_ERROR = Message.raw("Nie udalo sie pobrac danych serwera.").color(Color.red);

    // NagrodaCommand
    public static final Message NAGRODA_CHECKING = Message.raw("Sprawdzamy Twoj glos, prosze czekac...").color(Color.green);
    public static final Message NAGRODA_CLAIM_FAILED = Message.raw("Nie udalo sie odebrac nagrody, sprobuj pozniej").color(Color.red);
    public static final Message NAGRODA_SUCCESS = Message.raw("Nagroda za glosowanie zostala odebrana!").color(Color.green);

    public static Message nagrodaCooldown(long seconds) {
        return Message.raw("Musisz poczekac jeszcze " + seconds + " sekund.").color(Color.red);
    }

    // TestCommand
    public static final Message TEST_INFO = Message.raw("Ta komenda pozwala na przetestowanie nagrody\n").color(Color.green)
            .insert(Message.raw("Aby sprawdzic polaczenie pluginu z lista serwerow\n").color(Color.green))
            .insert(Message.raw("po prostu odbierz nagrode za pomocą /sh-nagroda").color(Color.green));

    // TokenCommand
    public static final Message TOKEN_USAGE = Message.raw("Uzycie: /sh-token <identyfikator>").color(Color.red);
    public static final Message TOKEN_SAVING = Message.raw("Zapisywanie tokena...").color(Color.yellow);
    public static final Message TOKEN_SUCCESS = Message.raw("Token zostal zapisany i konfiguracja przeladowana!").color(Color.green);
    public static final Message TOKEN_ERROR = Message.raw("Wystapil blad podczas zapisywania tokena!").color(Color.red);

    // ReloadCommand
    public static final Message RELOAD_LOADING = Message.raw("Przeladowywanie konfiguracji...").color(Color.yellow);
    public static final Message RELOAD_SUCCESS = Message.raw("Konfiguracja zostala pomyslnie przeladowana!").color(Color.green);
    public static final Message RELOAD_ERROR = Message.raw("Blad podczas przeladowywania konfiguracji!").color(Color.red);
    public static final Message RELOAD_ERROR_EXCEPTION = Message.raw("Wystapil blad podczas przeladowywania konfiguracji!").color(Color.red);

    private Consts() {
    }
}
