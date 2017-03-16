# PLATFORM

## API

### get(Class<?>)

`public static List<ExtensionContainer> get(Class<?> inter)`

Retourne la liste des implémentations d'une interface donnée en paramètre.

```
    List<ExtensionContainer> affichages = Framework.get(IAffichage.class);
    IAffichage affichageConsole = (IAffichage) affichages.get(0).getExtension();
```

### get(Class<?>, Class<?>)

`public static IExtension get(Class<?> inter, Class<?> impl)`

Retourne l'implémentation en fonction de son interface et de sa classe donnée en paramètre.

```
    IAffichage affichageConsole = Framework.get(IAffichage.class, Console.class);
```

### getExtension(Class<?>)

`public static IExtension getExtension(Class<?> inter)`

Retourne la première implémentation d'une interface donnée en paramètre.

```
    IAffichage affichage = Framework.getExtension(IAffichage);
```

## Plugins

Les plugins se situent dans le package `extensions`.

Chaque plugin possède un fichier `config.json` ayant le format suivant:

```
{
    "name": "Affichage", // Nom du plugin
    "type": "IAffichage", // Interface du plugin
    "autorun": false, // Charge le plugin au lancement de l'application si vrai
    "killable": true // Indique si ce plugin peut être kill au runtime
}
```

## Etat d'avancement

FINI
