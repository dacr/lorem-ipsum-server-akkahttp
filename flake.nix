{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-26.05";
    utils.url = "github:numtide/flake-utils";
    sbt.url = "github:zaninime/sbt-derivation";
    sbt.inputs.nixpkgs.follows = "nixpkgs";
  };

  outputs = { self, nixpkgs, utils, sbt }:
  utils.lib.eachDefaultSystem (system:
  let
    pkgs = import nixpkgs { inherit system; };

    jdk         = pkgs.jdk25;
    jdkHeadless = pkgs.jdk25_headless;

    devSbt    = pkgs.sbt.override { jre = jdk; };
    buildSbt  = pkgs.sbt.override { jre = jdkHeadless; };
    devMetals = pkgs.metals.override { jre = jdk; };
  in {
    # ---------------------------------------------------------------------------
    # nix develop
    devShells.default = pkgs.mkShell {
      buildInputs = [devSbt devMetals jdk pkgs.hello];
    };

    # ---------------------------------------------------------------------------
    # nix build
    # withOverrides also makes the dependencies derivation use the JDK 25 sbt, not the nixpkgs default one
    packages.default = (sbt.mkSbtDerivation.${system}.withOverrides { sbt = buildSbt; }) {
      pname = "nix-lorem-ipsum";
      version = builtins.elemAt (builtins.match ''[^"]+"(.*)".*'' (builtins.readFile ./version.sbt)) 0;
      depsSha256 = "sha256-4H38w7SAtz33KVt5Ufhw7RfGrJAgoUIN7rvDve994ck=";

      src = ./.;

      buildInputs = [buildSbt jdkHeadless pkgs.makeWrapper];

      # without the nix sandbox /tmp is shared between build users, keep sbt boot socket in the per build TMPDIR
      buildPhase = "sbt -Djava.io.tmpdir=$TMPDIR Universal/packageZipTarball";

      installPhase = ''
          mkdir -p $out
          tar xf target/universal/lorem-ipsum-server-akkahttp.tgz --directory $out
          makeWrapper $out/bin/lorem-ipsum-server-akkahttp $out/bin/nix-lorem-ipsum \
            --set JAVA_HOME ${jdkHeadless} \
            --set PATH ${pkgs.lib.makeBinPath [
              pkgs.gnused
              pkgs.gawk
              pkgs.coreutils
              pkgs.bash
              jdkHeadless
            ]}
      '';
    };

    # ---------------------------------------------------------------------------
    # simple nixos services integration
    nixosModules.default = { config, pkgs, lib, ... }: {
      options = {
        services.lorem-ipsum = {
          enable = lib.mkEnableOption "lorem-ipsum";
          user = lib.mkOption {
            type = lib.types.str;
            description = "User name that will run the lorem-ipsum service";
          };
          ip = lib.mkOption {
            type = lib.types.str;
            description = "Listening network interface - 0.0.0.0 for all interfaces";
            default = "127.0.0.1";
          };
          port = lib.mkOption {
            type = lib.types.int;
            description = "Service lorem-ipsum listing port";
            default = 8080;
          };
          url = lib.mkOption {
            type = lib.types.str;
            description = "How this service is known/reached from outside";
            default = "http://127.0.0.1:8080";
          };
          prefix = lib.mkOption {
            type = lib.types.str;
            description = "Service lorem-ipsum url prefix";
            default = "";
          };
        };
      };
      config = lib.mkIf config.services.lorem-ipsum.enable {
        systemd.services.lorem-ipsum = {
          description = "Lorem ipsum service";
          environment = {
            LOREM_IPSUM_LISTEN_IP   = config.services.lorem-ipsum.ip;
            LOREM_IPSUM_LISTEN_PORT = (toString config.services.lorem-ipsum.port);
            LOREM_IPSUM_PREFIX      = config.services.lorem-ipsum.prefix;
            LOREM_IPSUM_URL         = config.services.lorem-ipsum.url;
          };
          serviceConfig = {
            ExecStart = "${self.packages.${pkgs.system}.default}/bin/nix-lorem-ipsum";
            User = config.services.lorem-ipsum.user;
            Restart = "on-failure";
          };
          wantedBy = [ "multi-user.target" ];
        };
      };
    };
    # ---------------------------------------------------------------------------

  });
}
